#!/bin/bash
cd ..
find -iname \*.java | while read file ; do
printf "$file "
if ! grep -q Copyright $file
  then
    printf " - appending copyright.\n"
	cat egradle-other/copyright-java.txt $file >$file.new && mv $file.new $file
	#rm $file.new -f
	else
	printf ".\n"
  fi

done
cd egradle-other