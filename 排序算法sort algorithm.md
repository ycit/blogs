# 排序算法

### 冒泡排序

- 算法描述

  > 1.比较相邻元素，如果第一个比第二个大，就交换他们；
  >
  > 2.对每一对相邻元素做同样的工作，从开始一对到结尾的最后一对，这样在最后的元素会是最大的数；
  >
  > 3.针对所有元素重复以上步骤，除了最后一个
  >
  > 4.重复1-3步骤，直到排序完成；

- Java 实现

```java
    /**
     * 冒泡排序
     * 时间复杂度:平均O(n^2)
     * 空间复杂度:O(1)
     *
     * @param arr
     */
    public static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
```



### 选择排序

- 算法描述

  > 从未排序区开始，选择最小(大)元素，存放到排序序列起始位置，然后再从剩余未排序元素中重复该步骤，直到所有元素均排序完毕。

- Java 实现

```java
    /**
     * 选择排序
     *
     * @param arr
     */
    public static void selectionSort(int[] arr) {
        int minIndex, temp;
        for (int i = 0; i < arr.length - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[minIndex] > arr[j]) {
                    minIndex = j;
                }
            }
            temp = arr[i];
            arr[i] = arr[minIndex];
            arr[minIndex] = temp;
        }
    }
```



### 插入排序

- 算法描述

  > 1. 从第一个元素开始，该元素可以认为已经被排序
  >
  > 2. 取出下一个元素，在已经排序的元素序列中从后向前扫描
  > 3. 如果该元素（已排序）大于新元素，将该元素移到下一个位置
  > 4. 重复步骤3，直到找到已排序的元素小于或者等于新元素
  > 5. 将新元素插入到该元素
  > 6. 重复步骤2~5，直到排序结束

- Java 实现

```java
    public static void insertionSort(int[] arr) {
        int preIndex, current;
        for (int i = 1; i< arr.length; i++) {
            preIndex = i - 1;
            current =arr[i];
            while ( preIndex >= 0 && current < arr[preIndex]) {
                arr[preIndex + 1] = arr[preIndex];
                preIndex--;
            }
            arr[preIndex + 1] = current;
        }
    }
```



