<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${(name)!'此字符为空'}!
<br>
遍历数据模型stus中的list学生信息
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <td>出生日期</td>
    </tr>
    <#if stus??>
        <#list stus as stu>
            <tr>
                <td>${stu_index+1}</td>
                <td <#if stu.name=='小明'>style="background: cornflowerblue" </#if>>${stu.name}</td>
                <td>${stu.age}</td>
                <td <#if (stu.money > 300)>style="background: cornflowerblue" </#if>>${stu.money}</td>
                <td>${stu.birthday?string("yyyy年MM月dd日")}</td>
                <#--${today?string("yyyy年MM月")}-->
            </tr>
        </#list><br/>
    学生的个数:${stus?size}
    </#if>
</table>
<br/>
<br>
遍历stuMap
<br/>
姓名:${(stuMap.stu1.name)!''}<br/>
年龄:${(stuMap.stu1.age)!''}<br/>
key.stuMap?keys就是key列表
<#list stuMap?keys as k><br/>
<#--()!''如果不存在就显示空字符串-->
姓名:${(stuMap[k].name)!''}<br/>
年龄:${(stuMap[k].age)!''}<br/>
</#list>
</body>
</html>