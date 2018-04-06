#!/bin/bash

echo
echo "################################"
echo "# Prepare for deployment"
echo "################################"
echo 
# call copyright script etc. 
# its outside the root folder because of 
# problems with github license autodection...
./egradle-other/apply_copyright_info.sh
./egradle-other/apply_version_info.sh


