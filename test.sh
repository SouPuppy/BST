#!/bin/bash

cd "$(dirname "$0")"

javac BST.java || { echo "❌ 编译失败"; exit 1; }

pass=0
fail=0

for input_file in data/input_*.txt; do
    id=$(echo "$input_file" | grep -o '[0-9]\+')
    expected_output="data/output_${id}.txt"
    actual_output="data/tmp_output_${id}.txt"

    start=$(date +%s%3N)

    java BST < "$input_file" > "$actual_output"

    end=$(date +%s%3N)
    runtime=$((end - start))

    if diff -q <(tr -d '\r\n\t ' < "$actual_output") <(tr -d '\r\n\t ' < "$expected_output") > /dev/null; then
        echo "✅ Test $id passed  (${runtime}ms)"
        ((pass++))
    else
        echo "❌ Test $id failed  (${runtime}ms)"
        echo "   Input:    $input_file"
        echo "   Expected: $expected_output"
        echo "   Actual:   $actual_output"
        ((fail++))
    fi
done

rm -f ./*.class

echo ""
echo "🧪 Pass: $pass, Fail: $fail"
