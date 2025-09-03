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
    //��ǰϵͳĬ�ϵ��ļ���Ŀ¼Ϊ�û�Ŀ¼
    private static String currentPath = "D:/workspace_idea/a";

    public static void main(String[] args) {
        //ѭ����ʾ���˵���ֱ���û��˳�
        while (true) {
            System.out.println("===== �ļ������� =====");
            System.out.println("��ǰ·��: "+currentPath);
            System.out.println("1. ���Ŀ¼  2. �л�Ŀ¼  3. �����ļ�/Ŀ¼");
            System.out.println("4. ɾ����Ŀ  5. �����ļ�  6. ��������  7. �˳�");
            System.out.println("��ѡ��: ");
            int choice = sc.nextInt();
            if (choice < 1 || choice > 7) {
                System.out.println("����ѡ������ֻ������1~7�����֣���");
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
                    System.out.println("ллʹ�ã��ټ�");
                    sc.close();
                    return;
            }
        }
    }

    /*
    �ļ���������
    ֧�ְ����ƹؼ�������
    �ṩ����������Χ����ǰĿ¼�Ͱ�����Ŀ¼
     */
    private static void searchItem() {
        System.out.println("���������ؼ���: ");
        sc.nextLine();
        String keyword = sc.nextLine().trim().toLowerCase();
        System.out.println("1.��ǰĿ¼ 2.������Ŀ¼����ѡ��: ");
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

        //��ʾ�����������
        System.out.println("�ҵ� "+searchFiles.size()+" ��ƥ����:");
        searchFiles.forEach(file->{
            System.out.println((file.isDirectory()?"��Ŀ¼��":"���ļ���")+" "+file.getAbsolutePath());
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
            //�ڱ�����ǰ����Ԫ��ʱ��������ļ��л�Ҫ���еݹ��������������ݵ�ͬһ������
            if (f.isDirectory()) {
                searchSubItems(f, keyword, searchFiles);
            }
        }
    }

    /*
    �ļ���Ŀ¼ɾ��
    ֧��ɾ�������ļ���Ŀ¼
    ����Ŀ¼���õݹ�ɾ�����ԣ���ɾ��������ɾ��Ŀ¼����
    ����ɾ��ȷ�ϲ��裬��ֹ�����
     */
    private static void deleteItem() {
        System.out.println("����Ҫɾ��������:");
        sc.nextLine();
        String name = sc.nextLine();

        File item = new File(currentPath + File.separator + name);

        if (!item.exists()) {
            System.out.println("��Ŀ������");
            return;
        }

        System.out.println("ȷ��ɾ��? (y/n):");
        String choice = sc.nextLine().trim();
        if (choice.equals("y")) {

            boolean result = deleteProject(item);
            System.out.println(result?"ɾ���ɹ�":"ɾ��ʧ��");
        }else{
            System.out.println("��ȡ��");
        }

    }

    //�ݹ�ɾ��
    private static boolean deleteProject(File item) {
        //�����Ŀ¼����ɾ���������ļ�����Ŀ¼
        if (item.isDirectory()&&item.listFiles().length>0) {
            File[] childFiles = item.listFiles();
            if (childFiles != null) {
                for (File child : childFiles) {
                    //�ݹ�ɾ����������һ��ʧ����������ʧ��
                    if (!deleteProject(child)) {
                        return false;
                    }

                }
            }
        }

        //ֱ��ɾ���ļ����߿�Ŀ¼
        return item.delete();
    }

    /*
    �ļ���Ŀ¼����
    ֧�ִ������ļ�����Ŀ¼
    ����������飬���⸲��������Ŀ
    �����������п��ܳ��ֵ��쳣
     */
    private static void createItem() {
        System.out.println("1.�����ļ� 2.����Ŀ¼����ѡ��:");
        sc.nextLine();
        String type = sc.nextLine().trim();
        System.out.println("����������:");
        String name = sc.nextLine().trim();

        //��֤�ļ�������Ϊ��
        if (name.isEmpty()) {
            System.out.println("���Ʋ���Ϊ��");
            return;
        }
        //����File����
        File item = new File(currentPath + File.separator + name);
        //���ͬ������
        if (item.exists()) {
            System.out.println("�Ѵ���ͬ����Ŀ������");
            return;
        }
        try {
            //�������ʹ���
            boolean success = "1".equals(type)?item.createNewFile():item.mkdirs();
            System.out.println(success?"�����ɹ�":"����ʧ��");
        } catch (IOException e) {
            System.out.println("����ʧ��"+e.getMessage());
        }

    }

    //�л�Ŀ¼�ķ���
    private static void switchDirectory() {
        System.out.println("����Ŀ��·��(..������һ��):");
        sc.nextLine();
        String path = sc.nextLine();
        //�������Ŀ¼��������..������·�������·��
        File newDir = path.equals("..") ? new File(currentPath).getParentFile() :
                new File(path).isAbsolute() ? new File(path) : new File(currentPath + File.separator + path);

        //��֤Ŀ¼����Ч��
        if (newDir != null&& newDir.exists()&&newDir.isDirectory()) {
            currentPath = newDir.getAbsolutePath();
            System.out.println("���л���:"+currentPath);
        }else{
            System.out.println("Ŀ¼������");
        }

    }

    //Ŀ¼�������

    //չʾÿ����Ŀ�����ơ���С���޸�ʱ��ȹؼ���Ϣ
    //�����������ʾ�������⣬�ṩ�������Ӿ�����
    private static void browseDirectory() {
        //��������ǰ�ļ��е�File����
        File dir = new File(currentPath);

        if (!dir.exists()||!dir.isDirectory()) {
            System.out.println("��ЧĿ¼��");
            return;
        }


        File[] files = dir.listFiles();
        if (files == null||files.length==0) {
            System.out.println("Ŀ¼Ϊ��");
            return;
        }

        //��ʾĿ¼�����ݱ����С����Ϣ
        System.out.println("Ŀ¼����:");
        System.out.println("����    ����                          ��С           �޸�ʱ��");
        System.out.println("------------------------------------------------");
        //����ʾ��Ŀ¼
        for (File item : files) {
            if (item.isDirectory()) {
                printItemInfo(item);
            }
        }
        //��ʾ���ļ�
        for (File item : files) {
            if (item.isFile()) {
                printItemInfo(item);
            }
        }
    }

    private static void printItemInfo(File item) {
        //ȷ������
        String type = item.isDirectory() ? "Ŀ¼" : "�ļ�";
        //��С
        String size = item.isDirectory()?"-":item.length()+"B";
        //��ʽ���޸�ʱ��
        String time = sdf.format(new Date(item.lastModified()));
        //ƴ�Ӵ�ӡ��Ϣ�������ж���
        String line = padRight(type,6)+padRight(item.getName(),30)+padRight(size,15)+time;
        System.out.println(line);
    }

    //�������������ַ����Ҷ��벢���ո����ƶ�����
    private static String padRight(String str, int len) {
        //����ַ����������ضϲ��ӿո�
        if (str.length() >= len) {
            return str.substring(0, len - 1) + " ";
        }
        //����ȫ�ո�
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < len) {
            sb.append(" ");
        }
        return sb.toString();
    }

}
