package com.htetznaing.droidxbg;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class myFiles
{
    public static boolean HaveRoot = false;
    public static boolean HaveBB = false;
    public static boolean HaveTB = false;
    public static String FSerror = "";
    public String ErrStr = "";
    private StringBuilder errval = new StringBuilder();
    private String NEW_LINE = System.getProperty("line.separator");
    public String OutStr = "";
    private StringBuilder inval = new StringBuilder();
    private static final int BUFFER = 2048;
    private static OutputStream outstr;
    private static InputStream instr;
    private static InputStream errstr;
    private static Process process;

    public String GetPerms(String fname)
    {
        String result = "";
        StringBuilder sb = new StringBuilder();

        RootCmd("ls -l ", fname, sb, this.errval, false);

        this.ErrStr = this.errval.toString().trim();

        result = sb.substring(1, sb.indexOf(" ") - 1).toString();

        return result;
    }

    public boolean mv(String Oldname, String Newname)
    {
        clearinval();
        clearerrval();

        RootCmd("mv " + Oldname + " " + Newname, null, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public String Escape(String fname)
    {
        char[] ec = { ' ', '(', ')' };
        StringBuilder line = new StringBuilder();
        if ((fname.contains("\\ ")) || (fname.contains("\\(")) || (fname.contains("\\)"))) {
            return fname;
        }
        int ct = 0;
        line.append(fname);
        for (int i = 0; i < 3; i++) {
            for (int y = 0; y < fname.length(); y++) {
                if (fname.charAt(y) == ec[i])
                {
                    line.insert(y + ct, '\\');
                    ct++;
                }
            }
        }
        return line.toString();
    }

    public boolean cp(String Src, String Dest)
    {
        clearinval();
        clearerrval();

        RootCmd("cp " + Src + " " + Dest, null, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();
        FSerror = this.ErrStr + this.OutStr;
        if (FSerror.length() != 0)
        {
            Log.i("cp", "inside alt cp");
            clearinval();
            clearerrval();
            FSerror = "";

            byte[] buffer = new byte['?'];
            int num = 0;
            File fd = new File(Dest);
            File fs = new File(Src);
            String tf = "";
            if (fd.isDirectory()) {
                tf = Dest + Src.substring(Src.lastIndexOf("/") + 1, Src.length());
            } else {
                tf = Dest;
            }
            try
            {
                FileInputStream fin = new FileInputStream(Src);
                FileOutputStream fout = new FileOutputStream(tf);

                num = fin.read(buffer);
                while (num > 0)
                {
                    fout.write(buffer, 0, num);
                    num = fin.read(buffer);
                }
                fd.setLastModified(fs.lastModified());
                fin.close();
                fout.close();
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
            }
        }
        return FSerror.length() == 0;
    }

    public boolean Exists(String Filename)
    {
        File f = new File(Filename);

        return f.exists();
    }

    private static void copyFolder(File src, File dest)
            throws IOException
    {
        Log.i("cp", "inside copyfolder");
        if (src.isDirectory())
        {
            if (!dest.exists()) {
                dest.mkdirs();
            }
            String[] files = src.list();
            String[] arrayOfString1;
            int j = (arrayOfString1 = files).length;
            for (int i = 0; i < j; i++)
            {
                String file = arrayOfString1[i];

                File srcFile = new File(src, file);
                File destFile = new File(dest, file);

                copyFolder(srcFile, destFile);
                destFile.setLastModified(srcFile.lastModified());
            }
        }
        else
        {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte['?'];
            int length;
            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public boolean cpr(String Src, String Dest)
    {
        boolean ok = false;

        clearinval();
        clearerrval();

        RootCmd("cp -R " + Src + " " + Dest, null, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();
        ok = (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
        if (!ok)
        {
            File sf = new File(Src);
            File df = new File(Dest);
            try
            {
                copyFolder(sf, df);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                ok = false;
            }
        }
        return ok;
    }

    public boolean rm(String Fname)
    {
        clearinval();
        clearerrval();

        RootCmd("rm", Fname, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public boolean rmrf(String Fname)
    {
        clearinval();
        clearerrval();

        RootCmd("rm -R", Fname, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public boolean mkdir(String Dir)
    {
        clearinval();
        clearerrval();

        RootCmd("mkdir", Dir, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public boolean Symlink(String Linkname, String Filename)
    {
        String st = Linkname + " " + Filename;

        clearinval();
        clearerrval();

        RootCmd("ln -s " + st, null, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public long GetFileDate(String Fname)
    {
        File f = new File(Fname);

        long result = 0L;

        long T = f.lastModified();
        result = T;

        return result;
    }

    public String Sdcard()
    {
        File f = Environment.getExternalStorageDirectory();

        return f.getPath() + '/';
    }

    public String SdcardReady()
    {
        String st = "";

        st = Environment.getExternalStorageState();

        return st;
    }

    public boolean SetFileDate(String Fname, long Time)
    {
        boolean ok = false;
        File f = new File(Fname);

        ok = f.setLastModified(Time);

        return ok;
    }

    public String GetSymlink(String Fname)
    {
        StringBuilder sb = new StringBuilder();

        String result = "";

        clearerrval();

        RootCmd("ls -l ", Fname, sb, this.errval, false);

        this.ErrStr = this.errval.toString().trim();

        int i = sb.indexOf("/");
        if (i <= 0) {
            return "";
        }
        result = sb.substring(i, sb.length() - 1);

        return result;
    }

    public boolean isDir(String Path)
    {
        File f = new File(Path);

        boolean ok = f.isDirectory();

        return ok;
    }

    public boolean IsReadable(String Fname)
    {
        boolean ok = false;
        File f = new File(Fname);

        ok = f.canRead();

        return ok;
    }

    public boolean isSymlink(String Fname)
            throws IOException
    {
        File file = new File(Fname);
        File canon;
        if (file.getParent() == null)
        {
            canon = file;
        }
        else
        {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }

    public boolean IsWritable(String Fname)
    {
        boolean ok = false;
        File f = new File(Fname);

        ok = f.canWrite();

        return ok;
    }

    public String GetFileExt(String FName)
    {
        String ext = FName.substring(FName.lastIndexOf(".") + 1, FName.length());

        return ext;
    }

    public String GetFileType(String Fname)
    {
        MimeTypeMap mt = MimeTypeMap.getSingleton();

        String tst = GetFileExt(Fname);
        tst = tst.toLowerCase();

        String res = mt.getMimeTypeFromExtension(tst);
        if (res == null) {
            return "Unknown";
        }
        return res;
    }

    public double GetGigsFree(String Path)
    {
        StatFs stat = new StatFs(Path);

        long sdAvailSize = stat.getAvailableBlocks() *
                stat.getBlockSize();

        double gigaAvailable = sdAvailSize / 1.073741824E9D;
        return gigaAvailable;
    }

    public long GetFreespace(String Path)
    {
        StatFs stat = new StatFs(Path);

        long sdAvailSize = stat.getAvailableBlocks() *
                stat.getBlockSize();

        return sdAvailSize;
    }

    public String GetDiskstats(String Path)
    {
        StringBuilder sb = new StringBuilder();
        String tmp = "";

        clearerrval();

        RootCmd("df", Path, sb, this.errval, false);
        if (sb.toString() == "") {
            return "";
        }
        tmp = sb.toString();

        String[] tmp1 = tmp.split(" ");
        tmp = "";
        for (int x = 0; x < tmp1.length; x++) {
            tmp = tmp + tmp1[x] + " ";
        }
        return tmp;
    }

    public boolean chmod(String Filename, String permissions)
    {
        clearinval();
        clearerrval();

        RootCmd("chmod", permissions + " " + Filename, this.inval, this.errval, true);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public ArrayList<String> dirEntries(String Dir, Boolean Hidden)
    {
        ArrayList<String> lst = new ArrayList();

        StringBuilder sb = new StringBuilder();

        clearerrval();
        if (Hidden.booleanValue()) {
            RootCmd("ls -l -a " + Dir, null, sb, this.errval, false);
        } else {
            RootCmd("ls -l " + Dir, null, sb, this.errval, false);
        }
        this.ErrStr = this.errval.toString().trim();
        if (sb.length() == 0) {
            return lst;
        }
        String[] bdir = sb.toString().split(this.NEW_LINE);
        for (int x = 0; x < bdir.length; x++)
        {
            String tmp = bdir[x];

            Scanner sc = new Scanner(tmp);
            String tmp2 = "";

            int i = 0;
            while (sc.hasNext())
            {
                if ((i == 3) && ((tmp.charAt(0) == 'd') || (tmp.charAt(0) == 'l'))) {
                    tmp2 = tmp2 + "0" + " ";
                } else {
                    tmp2 = tmp2 + sc.next() + " ";
                }
                i++;
            }
            sc.close();

            tmp2 = tmp2.substring(0, tmp2.length() - 1);

            lst.add(tmp2);
        }
        return lst;
    }

    public String GetExtSd()
    {
        String dname = "";
        ArrayList<String> da = new ArrayList();
        int i = 0;
        int a1 = 0;

        StringBuilder sb = new StringBuilder();

        da = dirEntries("/storage", Boolean.valueOf(true));
        for (i = 0; i < da.size() - 1; i++)
        {
            sb.append((String)da.get(i));

            a1 = sb.indexOf("ext");
            if (a1 > -1)
            {
                dname = "/storage/" + sb.substring(a1, sb.length()) + "/";
                if (IsReadable(dname)) {
                    break;
                }
            }
            sb.setLength(0);
        }
        if (dname == "")
        {
            da = dirEntries("/mnt", Boolean.valueOf(true));
            for (i = 0; i < da.size() - 1; i++)
            {
                sb.append((String)da.get(i));

                a1 = sb.indexOf("ext");
                if (a1 > -1)
                {
                    dname = "/mnt/" + sb.substring(a1, sb.length()) + "/";
                    if (IsReadable(dname)) {
                        break;
                    }
                }
                sb.setLength(0);
            }
        }
        return dname;
    }

    public ArrayList<String> zipPeek(String zipname)
            throws IOException
    {
        ArrayList<String> ziplist = new ArrayList();
        String line = "";

        ZipInputStream z = new ZipInputStream(new FileInputStream(zipname));

        FSerror = "";
        ZipEntry ze;
        while ((ze = z.getNextEntry()) != null)
        {
            line = ze.getName() + ";" + ze.getCompressedSize() + ";" + ze.getTime();
            ziplist.add(line);
        }
        z.close();

        return ziplist;
    }

    public void extractZipFilesFromDir(String zipName, String fromDir, String toDir)
    {
        if (fromDir.charAt(fromDir.length() - 1) != '/') {
            fromDir = fromDir + "/";
        }
        String org_path = fromDir + zipName;

        extractZipFiles(org_path, toDir);
    }

    public void extractZipFiles(String zip_file, String directory)
    {
        byte[] data = new byte['?'];

        File f = new File(directory);
        if (directory.charAt(directory.length() - 1) != '/') {
            directory = directory + "/";
        }
        if (!f.exists()) {
            f.mkdirs();
        }
        Log.i(null, directory);
        String zipDir;
        String path;
        if (zip_file.contains("/"))
        {
            path = zip_file;
            String name = path.substring(path.lastIndexOf("/") + 1,
                    path.length() - 4);
            zipDir = directory + name + "/";
        }
        else
        {
            path = directory + zip_file;
            String name = path.substring(path.lastIndexOf("/") + 1,
                    path.length() - 4);
            zipDir = directory + name + "/";
        }
        Log.i(null, zipDir);

        new File(zipDir).mkdir();
        try
        {
            ZipInputStream zipstream = new ZipInputStream(new FileInputStream(path));
            ZipEntry entry;
            while ((entry = zipstream.getNextEntry()) != null)
            {
                String buildDir = zipDir;
                String[] dirs = entry.getName().split("/");
                if ((dirs != null) && (dirs.length > 0))
                {
                    for (int i = 0; i < dirs.length - 1; i++)
                    {
                        buildDir = buildDir + dirs[i] + "/";
                        new File(buildDir).mkdir();
                    }
                    Log.i(null, entry.getName());
                }
                int read = 0;
                FileOutputStream out = new FileOutputStream(
                        zipDir + entry.getName());
                while ((read = zipstream.read(data, 0, 2048)) != -1) {
                    out.write(data, 0, read);
                }
                zipstream.closeEntry();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void createZipFile(String path)
    {
        File dir = new File(path);
        String[] list = dir.list();
        String name = path.substring(path.lastIndexOf("/"), path.length());
        if (((!dir.canRead()) || (!dir.canWrite())) && (!HaveRoot)) {
            return;
        }
        int len = list.length;
        String _path;
        if (path.charAt(path.length() - 1) != '/') {
            _path = path + "/";
        } else {
            _path = path;
        }
        try
        {
            ZipOutputStream zip_out = new ZipOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(_path + name + ".zip"), 2048));
            for (int i = 0; i < len; i++) {
                zip_folder(new File(_path + list[i]), zip_out);
            }
            zip_out.close();
        }
        catch (FileNotFoundException e)
        {
            Log.e("File not found", e.getMessage());
        }
        catch (IOException e)
        {
            Log.e("IOException", e.getMessage());
        }
    }

    private void zip_folder(File file, ZipOutputStream zout)
            throws IOException
    {
        byte[] data = new byte['?'];
        if (file.isFile())
        {
            ZipEntry entry = new ZipEntry(file.getName());
            zout.putNextEntry(entry);
            BufferedInputStream instream = new BufferedInputStream(
                    new FileInputStream(file));
            int read;
            while ((read = instream.read(data, 0, 2048)) != -1)
            {
                zout.write(data, 0, read);
            }
            zout.closeEntry();
            instream.close();
        }
        else if (file.isDirectory())
        {
            String[] list = file.list();
            int len = list.length;
            for (int i = 0; i < len; i++) {
                zip_folder(new File(file.getPath() + "/" + list[i]), zout);
            }
        }
    }

    public String ReadTxtFile(String Fname)
            throws IOException
    {
        StringBuilder sb = new StringBuilder();

        clearerrval();

        RootCmd("cat", Fname, sb, this.errval, false);

        this.ErrStr = this.errval.toString().trim();

        return sb.toString();
    }

    public boolean WriteTxtFile(String Fname, String Txt)
            throws FileNotFoundException
    {
        clearinval();
        clearerrval();

        RootCmd("echo " + Txt + " > ", Fname, this.inval, this.errval, false);

        this.ErrStr = this.errval.toString().trim();
        this.OutStr = this.inval.toString().trim();

        return (this.ErrStr.length() == 0) && (this.OutStr.length() == 0);
    }

    public boolean Touch(String Fname)
    {
        File f = new File(Fname);
        boolean ok = false;
        Date dt = new Date();

        clearinval();
        clearerrval();
        if (f.exists()) {
            ok = f.setLastModified(dt.getTime());
        } else {
            try
            {
                ok = f.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage().trim();
            }
        }
        return ok;
    }

    private static boolean checked = false;
    private static String rootpref = "";
    public static String fsStdOut = "";

    private static void clearerr()
    {
        FSerror = "";
    }

    public static ArrayList<String> GetFileNames(String DirectoryPath) {
        File f = new File(DirectoryPath);
        f.mkdirs();
        File[] file = f.listFiles();

        ArrayList<String> arrayFiles = new ArrayList<>();
        if (file.length == 0)
            return null;
        else {
            for (int i=0; i<file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        Collections.sort(arrayFiles, String.CASE_INSENSITIVE_ORDER);
        return arrayFiles;
    }

    public static void GetRoot()
            throws InterruptedException
    {
        boolean ok = false;
        String st = "";String pre = "";
        File f = null;

        clearerr();

        f = new File("/system/bin/su");
        if (f.exists())
        {
            pre = "/system/bin/";
            rootpref = pre;
        }
        else
        {
            f = new File("/system/xbin/su");
            if (f.exists()) {
                pre = "/system/xbin/";
            }
            rootpref = pre;
        }
        try
        {
            Process process = Runtime.getRuntime().exec(rootpref + "su -c id");

            byte[] buffer = new byte['?'];

            StringBuilder StdErr = new StringBuilder();
            StringBuilder StdOut = new StringBuilder();

            instr = process.getInputStream();
            int count;
            while ((count = instr.read(buffer)) > 0)
            {
                StdOut.append(new String(buffer, 0, count, "UTF8"));
            }
            fsStdOut = StdOut.toString().trim();

            InputStream in = process.getErrorStream();

            while ((count = in.read(buffer)) > 0) {
                StdErr.append(new String(buffer, 0, count, "UTF8"));
            }
            process.waitFor();

            st = StdOut.toString();

            ok = st.indexOf("uid=0") >= 0;

            FSerror = StdErr.toString();
        }
        catch (IOException e)
        {
            FSerror = e.getMessage();
        }
        checkBB();

        HaveRoot = ok;
    }

    private static void checkBB()
    {
        File f = new File("/system/bin/busybox");

        HaveBB = f.exists();

        checked = true;
    }

    private static void checkTB()
    {
        File f = new File("/system/bin/toolbox");

        HaveTB = f.exists();

        checked = true;
    }

    public boolean RootCmd(String Command, String Args, StringBuilder StdOut, StringBuilder StdErr, boolean useBB)
    {
        boolean ok = false;
        int count = 0;
        byte[] buf = new byte['?'];
        String[] pref = { "/system/bin/", "/system/xbin/", "/system/sbin/" };
        File f = null;
        String pre = "";

        clearerr();
        int x = 0;
        for ( x = 0; x < 2; x++)
        {
            if (Command.indexOf(" ") < 1) {
                f = new File(pref[x] + Command.substring(0, Command.length()));
            } else {
                f = new File(pref[x] + Command.substring(0, Command.indexOf(" ")));
            }
            ok = f.exists();
            if (ok) {
                break;
            }
        }
        if (x > 2)
        {
            if (Command.indexOf(" ") < 1) {
                FSerror = "File not found " + Command;
            } else {
                FSerror = "File not found " + Command.substring(0, Command.indexOf(" "));
            }
            return false;
        }
        if (Command.contains("/")) {
            pre = "";
        } else {
            pre = pref[x];
        }
        if (!checked) {
            checkBB();
        }
        if (!HaveBB) {
            checkTB();
        }
        if ((HaveBB) && (useBB)) {
            Command = "/system/bin/busybox " + Command;
        } else if ((HaveTB) && (useBB)) {
            Command = "/system/bin/toolbox " + Command;
        } else {
            Command = pre + Command;
        }
        if (HaveRoot) {
            try
            {
                process = Runtime.getRuntime().exec(rootpref + "su");
                outstr = process.getOutputStream();
                instr = process.getInputStream();
                errstr = process.getErrorStream();

                String buffer = "PATH=/system/bin/:/system/xbin/\n";
                outstr.write(buffer.getBytes());
                outstr.flush();
                if ((Args == null) || (Args.length() == 0))
                {
                    Command = Command + "\n";

                    outstr.write(Command.getBytes());
                    outstr.flush();

                    Log.i("", Command);

                    outstr.write("exit\n".getBytes());
                    outstr.flush();

                    ok = true;
                }
                else
                {
                    String a = Command + " " + Args + "\n";

                    outstr.write(a.getBytes());
                    outstr.flush();

                    Log.i("", a);

                    outstr.write("exit\n".getBytes());
                    outstr.flush();

                    ok = true;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                return false;
            }
        } else {
            try
            {
                process = Runtime.getRuntime().exec("/system/bin/sh");
                outstr = process.getOutputStream();
                instr = process.getInputStream();
                errstr = process.getErrorStream();

                String buffer = "PATH=/system/bin/:/system/xbin/\n";
                outstr.write(buffer.getBytes());
                outstr.flush();
                if ((Args == null) || (Args.length() == 0))
                {
                    Command = Command + "\n";

                    Log.i("", Command);

                    outstr.write(Command.getBytes());
                    outstr.flush();

                    outstr.write("exit\n".getBytes());
                    outstr.flush();

                    ok = true;
                }
                else
                {
                    String a = Command + " " + Args + "\n";

                    outstr.write(a.getBytes());
                    outstr.flush();

                    Log.i("", a);

                    outstr.write("exit\n".getBytes());
                    outstr.flush();

                    ok = true;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                return false;
            }
        }
        if (StdOut != null) {
            try
            {
                while ((count = instr.read(buf)) > 0) {
                    StdOut.append(new String(buf, 0, count, "UTF8"));
                }
                fsStdOut = StdOut.toString().trim();
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                return false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                return false;
            }
        }
        if (StdErr != null) {
            try
            {
                while ((count = errstr.read(buf)) > 0) {
                    StdErr.append(new String(buf, 0, count, "UTF8"));
                }
                FSerror = StdErr.toString();
                if (FSerror != "") {
                    return false;
                }
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                return false;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                FSerror = e.getMessage();
                return false;
            }
        }
        try
        {
            process.waitFor();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            FSerror = e.getMessage();
            return false;
        }
        return ok;
    }

    private void clearinval()
    {
        this.inval.setLength(0);
    }

    private void clearerrval()
    {
        this.errval.setLength(0);
    }
}
