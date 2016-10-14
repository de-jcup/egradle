#!/bin/bash

STARTDIR=${pwd}
cd egradle-other
./apply_copyright_info.sh
./apply_version_info.sh

cd $STARTDIR

