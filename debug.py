import subprocess
import re

def get_elapsed_time():
    result = subprocess.run(
        ["/usr/bin/time", "-v", "java", "-Dfile.encoding=UTF-8", "-XX:+UseSerialGC", "-Xss64m", "-Xms1920m", "-Xmx1920m", "BST"],
        stdin=open("Large.txt", "r"),
        stdout=open("Output.txt", "w"),
        stderr=subprocess.PIPE,
        text=True
    )

    match = re.search(r"Elapsed \(wall clock\) time.*?: (\d+):(\d+\.\d+)", result.stderr)
    if match:
        minutes = int(match.group(1))
        seconds = float(match.group(2))
        return minutes * 60 + seconds
    else:
        return None

def main():
    total_time = 0
    runs = 5

    print("Running BST 100 times...")

    for i in range(1, runs + 1):
        elapsed_time = get_elapsed_time()
        if elapsed_time is not None:
            total_time += elapsed_time
            print(f"Run {i}: {elapsed_time:.4f}s")
        else:
            print(f"Run {i}: Failed to get elapsed time.")

    average_time = total_time / runs

    print(f"\nTotal Elapsed Time: {total_time:.4f}s")
    print(f"Average Elapsed Time: {average_time:.4f}s")

if __name__ == "__main__":
    main()
