#!/bin/bash
#Usage: testfiles.sh $INDIR $INEXT $OUTDIR $TARGET [$CMD1 $EXT1]

if [ -n "$4" ]; then
	TARGET=$4
else
	echo "usage: testfiles.sh INDIR INEXT OUTDIR TARGET [CHECKCMD [EXT]]"
	echo " e.g., testfiles.sh test/bun bun build/test py python"
        exit 1
fi

INDIR=$1
INEXT=$2
OUTDIR=$3

CMD1=$5
if [ -n "$6" ]; then
	EXT1=$6
else
	EXT1=$4
fi

if [ -z $BUNJAR ]; then
        BUNJAR='libbun-0.1.jar'
fi

OUTLOG="$OUTDIR/$TARGET.log"
OUTLOG1="$OUTLOG.1"
OUTLOG2="$OUTLOG.2"

if [ -f $OUTLOG ]; then
	rm -f "$OUTLOG" 
	rm -f "$OUTLOG1"
	rm -f "$OUTLOG2"
fi

for SRCPATH in $INDIR/*.$INEXT
do
	SRCFILE=`basename $SRCPATH .$INEXT`
	OUTPATH="$OUTDIR/$SRCFILE"
	echo "" >> $OUTLOG2
	echo "java -ea -jar $BUNJAR -t $TARGET -o $OUTPATH $SRCPATH" >> $OUTLOG2
	java -ea -jar $BUNJAR -t $TARGET -o $OUTPATH $SRCPATH >> $OUTLOG2
	if [ $? -ne 0 ]; then
		echo "[FAILED] $SRCFILE"  >> $OUTLOG1
		echo "[FAILED] $SRCFILE"
		continue
	fi
	if [ -n "$CMD1" ]; then
		echo "$CMD1 $OUTPATH.$EXT1" >> $OUTLOG2
		$CMD1 "$OUTPATH.$EXT1" >> $OUTLOG2
	fi
	if [ $? -ne 0 ]; then
		echo "[FAILED] $CMD1 $SRCFILE"  >> $OUTLOG1
		echo "[FAILED] $CMD1 $SRCFILE"
	else
		echo "[OK] $SRCFILE" >> $OUTLOG1
		echo "[OK] $SRCFILE"
	fi
done

cat $OUTLOG1 $OUTLOG2 > $OUTLOG
rm -f $OUTLOG1
rm -f $OUTLOG2

