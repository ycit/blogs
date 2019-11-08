## elasticsearch

### 分析器（analyzer）

> 组成：字符过滤器，分词器，token 过滤器
>
> 内置分析器：
>
> - standard
>
>   tokenizer： standard tokenizer
>
>   token filter：standard token filter |  lowercase token filter  | stop token filter
>
> - simple
>
>   tokenizer：lowercase tokenizer
>
> - whitespace
>
>   tokenizer：whitespace tokenizer
>
> - stop
>
>   tokenizer：lower case tokenizer
>
>   token filter： stop token filter
>
> - keyword
>
> - pattern
>
> - language
>
> - fingerprint

#### 字符过滤器(character filters)

> 在传给分词器之前预处理字符流 ：char_filter
>
> 内置的字符过滤器：
>
> - HTML Strip Character Filter:  html_strip
> - Mapping Character Filter：
>
> - Pattern Replace Character Filter
>
>   ```json
>   {
>       "settings":{
>           "analysis":{
>               "analyzer":{
>                   "my_analyzer":{
>                       "tokenizer":"standard",
>                       "char_filter":["my_char_filter"]
>                   }
>               },
>               "char_filter":{
>                   "my_char_filter":{
>                       "type":"pattern_replace",
>                       "pattern":"(\\d+)-(?=\\d)",
>                       "replacement":"$1_"
>                   }
>               }
>           }
>       }
>   }
>   ```
>
>   ```json
>   POST  my_index/_analyze
>   {
>       "analyzer":"my_analyzer",
>       "text":"num is 123-456-789"
>   }
>   
>   terms： [num，is 123_456_789]
>   ```
>
>   

#### 分词器(tokenizer)

> 分词器接收字符流，并将字符流拆分为单个 token,并输出 token 流。例如，一个空格分词器会将文本按照空格拆分。
>
> 基于单词的内置分词器：
>
> - 标准分词器：standard
>
>   根据单词边界将文本拆分，并删除标点符号
>
>   ```json
>   POST _analyze
>   {
>     "tokenizer": "standard",
>     "text": "The 2 QUICK Brown-Foxes jumped over the lazy dog's bone."
>   }
>   
>   terms: [ The, 2, QUICK, Brown, Foxes, jumped, over, the, lazy, dog's, bone ]
>   ```
>
> - 字母分词器：letter
>
>   每当遇到不是字母的字符时，会进行拆分，并删除非字母的字符
>
> - 小写分词器：lowercase
>
>   在字母分词器的基础上，同时将所有字母转为小写
>
> - 空格分词器：whitespace
>
>   每当遇到空格时，会进行拆分
>
> - uax_url_emali 分词器：
>
>   在标准分词器的基础上，会将 url 和 电子邮件保留为单个 token
>
> - 经典分词器：classic
>
>   基于语法的英文分词器
>
> 结构化文本分词器：标识符，电子邮件地址，邮政编码 和路径
>
> - keyword 分词器：keyword
>
>   空分词器，输入和输出相同；可以
>
> - pattern 分词器：pattern
>
> - path 分词器：path_hierarchy

#### token 过滤器(token filters)

> 接收来自于 分词器的 的 token 流，可以修改 token（例如 lowercasing），删除token（移除 停顿词），或者增加 token（例如同义词）。filter
>
> 内置token 过滤器比较多