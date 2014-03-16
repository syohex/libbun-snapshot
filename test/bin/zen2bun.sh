
for fname in */*.zen; do
	#echo $fname
	mv $fname ${fname%.zen}.bun
done
