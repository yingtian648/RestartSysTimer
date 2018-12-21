### ReStartSysTimer

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
