package FileManagement;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class FileTest {
    private static Scanner sc = new Scanner(System.in);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    //当前系统默认的文件夹目录为用户目录
    private static String currentPath = "D:/workspace_idea/a";

    public static void main(String[] args) {
        //循环显示主菜单，直到用户退出
        while (true) {
            System.out.println("===== 文件管理工具 =====");
            System.out.println("当前路径: "+currentPath);
            System.out.println("1. 浏览目录  2. 切换目录  3. 创建文件/目录");
            System.out.println("4. 删除项目  5. 搜索文件  6. 批量操作  7. 退出");
            System.out.println("请选择: ");
            int choice = sc.nextInt();
            if (choice < 1 || choice > 7) {
                System.out.println("输入选项有误，只能输入1~7的数字！！");
                continue;
            }

            switch (choice) {
                case 1: browseDirectory();break;
                case 2: switchDirectory();break;
                case 3: createItem();break;
                case 4: deleteItem();break;
                case 5: searchItem();break;
                case 6: break;
                case 7:
                    System.out.println("谢谢使用，再见");
                    sc.close();
                    return;
            }
        }
    }

    /*
    文件搜索功能
    支持按名称关键词搜索
    提供两种搜索范围：当前目录和包含子目录
     */
    private static void searchItem() {
        System.out.println("输入搜索关键词: ");
        sc.nextLine();
        String keyword = sc.nextLine().trim().toLowerCase();
        System.out.println("1.当前目录 2.包含子目录，请选择: ");
        int choice = Integer.parseInt(sc.nextLine().trim());
        ArrayList<File> searchFiles = new ArrayList<>();

        if (choice == 1) {
            File file = new File(currentPath);
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.getName().contains(keyword)) {
                    searchFiles.add(f);
                }
            }
        }else {
            searchSubItems(new File(currentPath),keyword,searchFiles);
        }

        //显示最后的搜索结果
        System.out.println("找到 "+searchFiles.size()+" 个匹配项:");
        searchFiles.forEach(file->{
            System.out.println((file.isDirectory()?"【目录】":"【文件】")+" "+file.getAbsolutePath());
        });
    }

    private static void searchSubItems(File file, String keyword, ArrayList<File> searchFiles) {
        if (!file.isDirectory()) {
            return;
        }
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.getName().contains(keyword)) {
                searchFiles.add(f);
            }
            //在遍历当前层子元素时，如果是文件夹还要进行递归搜索，保存数据到同一个集合
            if (f.isDirectory()) {
                searchSubItems(f, keyword, searchFiles);
            }
        }
    }

    /*
    文件和目录删除
    支持删除单个文件或目录
    对于目录采用递归删除策略，先删除内容再删除目录本身
    包含删除确认步骤，防止误操作
     */
    private static void deleteItem() {
        System.out.println("输入要删除的名称:");
        sc.nextLine();
        String name = sc.nextLine();

        File item = new File(currentPath + File.separator + name);

        if (!item.exists()) {
            System.out.println("项目不存在");
            return;
        }

        System.out.println("确定删除? (y/n):");
        String choice = sc.nextLine().trim();
        if (choice.equals("y")) {

            boolean result = deleteProject(item);
            System.out.println(result?"删除成功":"删除失败");
        }else{
            System.out.println("已取消");
        }

    }

    //递归删除
    private static boolean deleteProject(File item) {
        //如果是目录，先删除所有子文件和子目录
        if (item.isDirectory()&&item.listFiles().length>0) {
            File[] childFiles = item.listFiles();
            if (childFiles != null) {
                for (File child : childFiles) {
                    //递归删除子项，如果有一项失败了则整体失败
                    if (!deleteProject(child)) {
                        return false;
                    }

                }
            }
        }

        //直接删除文件或者空目录
        return item.delete();
    }

    /*
    文件和目录创建
    支持创建新文件或新目录
    包含重名检查，避免覆盖现有项目
    处理创建过程中可能出现的异常
     */
    private static void createItem() {
        System.out.println("1.创建文件 2.创建目录，请选择:");
        sc.nextLine();
        String type = sc.nextLine().trim();
        System.out.println("请输入名称:");
        String name = sc.nextLine().trim();

        //验证文件名不能为空
        if (name.isEmpty()) {
            System.out.println("名称不能为空");
            return;
        }
        //创建File对象
        File item = new File(currentPath + File.separator + name);
        //检查同名问题
        if (item.exists()) {
            System.out.println("已存在同名项目！！！");
            return;
        }
        try {
            //根据类型创建
            boolean success = "1".equals(type)?item.createNewFile():item.mkdirs();
            System.out.println(success?"创建成功":"创建失败");
        } catch (IOException e) {
            System.out.println("创建失败"+e.getMessage());
        }

    }

    //切换目录的方法
    private static void switchDirectory() {
        System.out.println("输入目标路径(..返回上一级):");
        sc.nextLine();
        String path = sc.nextLine();
        //输入的新目录，可能是..，绝对路径，相对路径
        File newDir = path.equals("..") ? new File(currentPath).getParentFile() :
                new File(path).isAbsolute() ? new File(path) : new File(currentPath + File.separator + path);

        //验证目录的有效性
        if (newDir != null&& newDir.exists()&&newDir.isDirectory()) {
            currentPath = newDir.getAbsolutePath();
            System.out.println("已切换到:"+currentPath);
        }else{
            System.out.println("目录不存在");
        }

    }

    //目录浏览功能

    //展示每个项目的名称、大小、修改时间等关键信息
    //解决了中文显示对齐问题，提供清晰的视觉体验
    private static void browseDirectory() {
        //创建出当前文件夹的File对象
        File dir = new File(currentPath);

        if (!dir.exists()||!dir.isDirectory()) {
            System.out.println("无效目录！");
            return;
        }


        File[] files = dir.listFiles();
        if (files == null||files.length==0) {
            System.out.println("目录为空");
            return;
        }

        //显示目录的内容标题大小等信息
        System.out.println("目录内容:");
        System.out.println("类型    名称                          大小           修改时间");
        System.out.println("------------------------------------------------");
        //先显示子目录
        for (File item : files) {
            if (item.isDirectory()) {
                printItemInfo(item);
            }
        }
        //显示子文件
        for (File item : files) {
            if (item.isFile()) {
                printItemInfo(item);
            }
        }
    }

    private static void printItemInfo(File item) {
        //确定类型
        String type = item.isDirectory() ? "目录" : "文件";
        //大小
        String size = item.isDirectory()?"-":item.length()+"B";
        //格式化修改时间
        String time = sdf.format(new Date(item.lastModified()));
        //拼接打印信息，保持列对齐
        String line = padRight(type,6)+padRight(item.getName(),30)+padRight(size,15)+time;
        System.out.println(line);
    }

    //辅助方法：将字符串右对齐并填充空格，至制定长度
    private static String padRight(String str, int len) {
        //如果字符串过长，截断并加空格
        if (str.length() >= len) {
            return str.substring(0, len - 1) + " ";
        }
        //否则补全空格
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < len) {
            sb.append(" ");
        }
        return sb.toString();
    }

}
