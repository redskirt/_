#include <stdio.h>
#include <stdlib.h>
//============================================================================
// Name        : bootcamp.c
// Author      : Matthew
// Version     :
//============================================================================

/* 1. C语言实现整形数组求和 */

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
//	int data[] = {1, 1, 1, 4, 3, 5};
//	int size = sizeof(data) / sizeof(data[0]);
//	printf("result: %d\n", count(data, size));
//	printf("result: %d", count2(data, size));

	/* 2. 输入流求和 */
	int i;
	int sum = 0;
	char c;

	printf("输入一组数字，用空格分隔：");
	while(scanf("%d", &i) == 1) {
		sum += i;
		while((c = getchar()) == ' '); // 屏蔽空格
		if(c == '\n') // 回车
			break;
		// 将变量c中存放的字符退回stdin（输入流）
		ungetc(c, stdin);
	}
	printf("result: %d\n", sum);
	system("pause");
	return 0;
}
