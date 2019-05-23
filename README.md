# tesseract-demo
OCR 学习


## TODO
1. 标记新字模的时候，可以和之前的字模做相似度对比，实现自动标记。
```java
// 大致算法：
// 取一个未标记的字模 newDct ，找出等宽等高的已标记的所有字模 dctList
for(Dct dct : dctList){
  // 计算两个字模像素点相同的百分率
  double p = compare(dct, newDct);
}
// 将未标记的字模，标记为最相近的那个字模的值
```
`当然，未必准确，还需要人工审核。`
