#!/bin/bash

cd "`dirname $0`"

OPTIONS='-alpha on -background none'

for ROTATION in 0 45 90 135 180 225 270 315
do
	convert src/space/img/ship.png $OPTIONS -rotate $ROTATION src/space/img/ship-$ROTATION.png
	convert src/space/img/mother.png $OPTIONS -rotate $ROTATION src/space/img/mother-$ROTATION.png
	convert src/space/img/colony.png $OPTIONS -rotate $ROTATION src/space/img/colony-$ROTATION.png
	convert src/space/img/shipn.png $OPTIONS -rotate $ROTATION src/space/img/shipn-$ROTATION.png
	convert src/space/img/mothern.png $OPTIONS -rotate $ROTATION src/space/img/mothern-$ROTATION.png
	convert src/space/img/colonyn.png $OPTIONS -rotate $ROTATION src/space/img/colonyn-$ROTATION.png
	convert src/space/img/shipf.png $OPTIONS -rotate $ROTATION src/space/img/shipf-$ROTATION.png
	convert src/space/img/motherf.png $OPTIONS -rotate $ROTATION src/space/img/motherf-$ROTATION.png
	convert src/space/img/colonyf.png $OPTIONS -rotate $ROTATION src/space/img/colonyf-$ROTATION.png
done

