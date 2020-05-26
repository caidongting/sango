## kotlin 编程规范

### 代码规范
- 使用驼峰命名法(class 大驼峰，其余小驼峰)
- `Enum` 成员必须全大写
- 除专有名词外不要使用缩写
- 使用空格代替tab，使用2个空格，使代码更加紧凑
- `{`左边不要换行，`}`右边一般需要换行
```kotlin
fun xx() {
}

val lambda = map {  }
```
- 函数返回类型除 `Unit` 外必须明显式指定
- top-level function or property 必须明确各种类型且确认是否必要，大部分可以用 `internel`限制作用域

### 默认约定
#### 1. protobuf
- 采用默认生成的规则，文件以`proto_xxx.proto`的形式，`package`指定生成的java文件位置。
- 来自客户端的消息以后缀`request`，返回给客户端的消息以后缀`response`

#### 2. 数据库 hibernate
所有的table都与一个entity对应，entity统一继承`IEntity`接口，用于后面的使用

#### 3. entityWrapper
用于包装`Entity`，同时处理数据格式，便于游戏内部使用。使用`toEntity()`转化为实体存储，`toDisplay()` 转化为前端信息展示

#### 4. 使用property而不是函数：
- O(1)复杂度
- 底层逻辑简单
- 缓存的数据

其余情况一律使用函数。