## 编程规范

### 代码规范
- 使用空格代替tab，使用2个空格，使代码更加紧凑
- `{`左边不要换行，`}`右边一般需要换行
```kotlin
fun xx() {
}

val lambda = map {  }
```
### 默认约定
#### 1. protobuf
采用默认生成的规则，文件以`proto_xxx.proto`的形式，`package`指定生成的java文件位置。

#### 2. 数据库 hibernate
所有的table都与一个entity对应，entity统一继承`IEntity`接口，用于后面的使用

#### 3. entityWrapper
用于包装`Entity`，同时处理数据格式，便于游戏内部使用。使用`toEntity()`转化为实体存储，`toDisplay()` 转化为前端信息展示

#### 4. 使用property而不是函数：
- O(1)复杂度
