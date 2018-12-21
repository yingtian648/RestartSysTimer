### ReStartSysTimer
1.适用范围：Android os 6.0以下<br>
2.系统已经root<br>

### 功能
1.定时重启手机Android系统<br>
2.采用1px宽高的悬浮窗来“隐藏”，避免容易被系统杀死<br>

### 核心代码
```
//重启方法 3 root之后可以重启成功
    public void rebootAction() {
        String cmd = "su -c reboot";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error! Fail to reboot.", Toast.LENGTH_SHORT).show();
        }
    }
```
### 其他
1.在app目录下有已经打包好的app，分别重启时间是1小时、2小时、4小时<br>
