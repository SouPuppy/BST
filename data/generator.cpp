#include <bits/stdc++.h>
using namespace std;

#define EPOCHS 5
const int sizes[EPOCHS] = {13, 100, 1000, 10000, 100000};
int arr[100000];


void gen(int id, int size) {
    for (int i = 0; i < size; i++) {
        arr[i] = i + 1;
    }
    shuffle(arr, arr + size, default_random_engine(random_device{}()));

    string filename = "input_" + to_string(id) + ".txt";
    ofstream fout(filename);
    for (int i = 0; i < size; i++) {
        fout << arr[i] << ",\n"[i + 1 == size];
    }
    fout.close();
}

struct Node {
    Node *left, *right;
    int val;

    Node() : left(NULL), right(NULL), val(0) {}

    void insert(int v) {
        if (val == 0 && left == NULL && right == NULL) {
            val = v;
            return;
        }
        if (v < val) {
            if (!left) left = new Node();
            left->insert(v);
        } else {
            if (!right) right = new Node();
            right->insert(v);
        }
    }
};

void get_post(Node *root, int *output, int &index) {
    if (!root) return;
    get_post(root->left, output, index);
    get_post(root->right, output, index);
    output[index++] = root->val;
}

void process(int id, int size) {
    Node *root = new Node();
    for (int i = 0; i < size; i++) {
        root->insert(arr[i]);
    }

    int idx = 0;
    int post_array[100000];
    get_post(root, post_array, idx);

    string filename = "output_" + to_string(id) + ".txt";
    ofstream fout(filename);
    for (int i = 0; i < size; i++) {
        fout << post_array[i] << "\n";
    }
    fout.close();
}

void test() {
    ifstream fin("Input.txt");

    string line;
    getline(fin, line); // 读取整行
    fin.close();

    stringstream ss(line);
    string token;
    int idx = 0;
    while (getline(ss, token, ',')) {
        arr[idx++] = stoi(token);     
    }

    Node *root = new Node();
    for (int i = 0; i < idx; i++) {
        root->insert(arr[i]);
    }

    int out_idx = 0;
    int post_array[100000];
    get_post(root, post_array, out_idx);

    ofstream fout("Output.txt");
    for (int i = 0; i < out_idx; i++) {
        fout << post_array[i] << "\n";
    }
    fout.close();
}


int main() {
	test();
	for (int i = 0; i < EPOCHS; i++) {
        gen(i, sizes[i]);
        process(i, sizes[i]);
    }
    return 0;
}
