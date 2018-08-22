#include <iostream>
//============================================================================
// Name        : bootcamp.cpp
// Author      : Matthew
// Version     :
//============================================================================

// C++所有使用的标识符（类、函数、对象等名称）都是在一个特殊的名空间std中定义的。
// 不使用namespace时的引用方式：std::cout
using namespace std;

/* 1. C++语言实现整形数组求和 */

// Note that: C语言中数组传递时仅传递指针，而不是数组本身！
// 函数参数声明为数组或指针效果相同。
int count(int array[], int size) {
	int sum = 0;
	for(int i=0; i<size; i++) {
		sum += array[i];
	}
	return sum;
}

// 使用传递指针方式
int count2(int *array, int size) {
	int sum = 0;
	for(int i=0; i<size; i++) {
		sum += *array++;
	}
	return sum;
}

int main() {
//	int data[] = {1, 1, 1, 4, 5, 5};
//	int size = sizeof(data) / sizeof(data[0]);
//	cout << "result: %d\n" << count(data, size) << endl;
//	cout << "result: %d\n" << count2(data, size) << endl;

	/* 2. 输入流求和 */
	int i;
	int sum = 0;

	cout << "输入一组数字，用空格分隔：" << endl;
	// cin >> i，提取操作，一次从输入流中提取一个整数
	while(cin/*istream类型*/ >> i) {
		sum += i;
		while(cin.peek() == ' ')
			cin.get();
		if(cin.peek() == '\n') // 回车
			break;
	}
	cout << "result: %d\n" << sum << endl;
	return 0;
}
