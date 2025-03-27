#!/bin/bash

# 进入脚本所在目录
cd "$(dirname "$0")"

# 编译 Java 文件
javac BST.java || { echo "❌ 编译失败"; exit 1; }

pass=0
fail=0

for input_file in data/input_*.txt; do
    id=$(echo "$input_file" | grep -o '[0-9]\+')
    expected_output="data/output_${id}.txt"
    actual_output="data/tmp_output_${id}.txt"

    # 记录开始时间（毫秒）
    start=$(date +%s%3N)

    # 运行 Java 程序
    java BST < "$input_file" > "$actual_output"

    # 记录结束时间（毫秒）
    end=$(date +%s%3N)
    runtime=$((end - start))

    # 比较输出结果（忽略空白字符）
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

# 清理 .class 文件
rm -f ./*.class

echo ""
echo "🧪 测试完成：$pass 通过，$fail 失败"
