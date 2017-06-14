#!/bin/bash

RED='\033[0;31m'
LIGHT_RED='\033[1;31m'
LIGHT_GREEN='\033[1;32m'
BROWN='\033[0;33m'
NC='\033[0m' # No Color


function ignore(){

# when no operands given
if [ -z "$1" ]
then
	echo "Missing parameters"
	cat "README_apply_version_info.txt" 
	echo 
	echo 
	exit
fi
}
cd .. -> /dev/null
EGRADLE_MAINFOLDER=$(pwd)

#if [ $OSTYPE == "msys" ]; then
#	cd .. -> /dev/null # egradle parent folder
#	echo "changing rights for windows in:" pwd
#    # Min gw (windows) - we need to make egradle accessible
#	chmod -R a+rw EGRADLE_MAINFOLDER
#	cd $EGRADLE_MAINFOLDER
#else
#	echo "Not windows but '$OSTYPE'"
#fi

###############################
# SEARCH OLD  VERSION
###############################
cd egradle-plugin-branding
echo Scanning for old version
#http://stackoverflow.com/questions/1665549/have-sed-ignore-non-matching-lines (p command ...)
OLD_VERSION=$(echo $a | sed -nr 's/^Bundle-Version: ([0-9]+\.[0-9]+\.[0-9]+.*)/\1/p' ./META-INF/MANIFEST.MF)
echo
echo -e "Found OLD-Version=${LIGHT_GREEN}$OLD_VERSION${NC}"

###############################
# ENTER new VERSION
###############################
echo -e "${LIGHT_RED}Please enter new version"
read NEW_VERSION
#echo "NEW-Version=$NEW_VERSION"
echo -e "${NC}"
if [ -z "$NEW_VERSION" ]
then
	echo "Empty version - break"
	exit
fi
if [ "$NEW_VERSION" == "$OLD_VERSION" ]
then
	echo "Same version - break"
	exit
fi
# check no 1.0 because of xml version="1.0"...
if [ "$NEW_VERSION" == "1.0" ]
then
	echo "version='1.0' is dangerous - would change xml versions too!"
	exit
fi


###############################
# UPDATE ....
###############################
cd $EGRADLE_MAINFOLDER
echo 
echo -e "Starting update in folder:${BROWN}"
echo 
pwd
echo -e "${NC}"
echo
echo "###############################"
echo "# reading manifest files"
echo "###############################"
find -iname MANIFEST.MF | while read file ; do 
	echo -e "${BROWN}$file${NC}"
	
	cat "$file" | \
		sed -i 's|Bundle-Version: '$OLD_VERSION'|Bundle-Version: '$NEW_VERSION'|' "$file"
		# if having dependency to main plugin change this too
		sed -i 's|de.jcup.egradle.eclipse.plugin.main;bundle-version="'$OLD_VERSION'"|de.jcup.egradle.eclipse.plugin.main;bundle-version="'$NEW_VERSION'"|' $file
	# Bugfix mingw - sed does not work correctly and files are remaining read only
	chmod 0755 $file
done
echo
echo "###############################"
echo "# reading about.html files"
echo "###############################"
find -iname about.html | while read file ; do 
	echo -e "${BROWN}$file${NC}"
	cat "$file" | \
		sed -i 's|<p>Version: '$OLD_VERSION'</p>|<p>Version: '$NEW_VERSION'</p>|' "$file"
		
	# Bugfix mingw - sed does not work correctly and files are remaining read only
	chmod 0755 $file
done
echo
echo "###############################"
echo "# reading feature.xml files"
echo "###############################"
find -iname feature.xml | while read file ; do 
	echo -e "${BROWN}$file${NC}"
	# <?xml version="1.0" will not be influcenced because we have always minor.major.patch version numbers...
	cat "$file" | \
		sed -i 's|version="'$OLD_VERSION'"|version="'$NEW_VERSION'"|' "$file"
	
	# Bugfix mingw - sed does not work correctly and files are remaining read only
	chmod 0755 $file
done

echo
echo "###############################"
echo "# reading about.ini files"
echo "###############################"
find -iname about.ini | while read file ; do 
	echo -e "${BROWN}$file${NC}"
	cat "$file" | \
		sed -i 's|Version '$OLD_VERSION'|Version '$NEW_VERSION'|' "$file"
	
	# Bugfix mingw - sed does not work correctly and files are remaining read only
	chmod 0755 $file
done

echo
echo "###############################"
echo "# new feature to update site"
echo "###############################"
cd $EGRADLE_MAINFOLDER/egradle-updatesite
find -iname site.xml | while read file ; do 
	echo -e "${BROWN}$file${NC}"
	
	# when beta is contained we use beta category...
	if [[ $NEW_VERSION == *"beta"* ]]
	then
	  CONTENT='<feature url=\"features/de.jcup.egradle.eclipse.feature_'$NEW_VERSION'.jar\" id=\"de.jcup.egradle.eclipse.feature\" version=\"'$NEW_VERSION'\"><category name=\"site.egradle.category.beta\"/></feature><feature url=\"features/de.jcup.egradle.eclipse.feature.editor_'$NEW_VERSION'.jar\" id=\"de.jcup.egradle.eclipse.feature.editor\" version=\"'$NEW_VERSION'\"><category name=\"site.egradle.category.beta\"/></feature>'
	 else 
	  CONTENT='<feature url=\"features/de.jcup.egradle.eclipse.feature_'$NEW_VERSION'.jar\" id=\"de.jcup.egradle.eclipse.feature\" version=\"'$NEW_VERSION'\"><category name=\"site.egradle.category.stable\"/></feature><feature url=\"features/de.jcup.egradle.eclipse.feature.editor_'$NEW_VERSION'.jar\" id=\"de.jcup.egradle.eclipse.feature.editor\" version=\"'$NEW_VERSION'\"><category name=\"site.egradle.category.stable\"/></feature>'
	 fi
	echo "Update site content=$CONTENT"
	# <?xml version="1.0" will not be influcenced because we have always minor.major.patch version numbers...
	UPC=$(echo $CONTENT | sed 's/\//\\\//g')
	sed -i "/<\/site>/ s/.*/${UPC}\n&/" $file
	
	# Bugfix mingw - sed does not work correctly and files are remaining read only
	chmod 0755 $file
	
done

cd $EGRADLE_MAINFOLDER/egradle-other

echo
echo "###############################"
echo "# MANUAL WORK TO DO:"
echo "###############################"
echo "- check if SDK is build in expected version (SDK has dependency to main and can be changed )"