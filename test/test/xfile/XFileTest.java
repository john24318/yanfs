/*
 * Copyright (c) 2009, 20011
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package test.xfile;

import java.io.*;
import com.sun.xfile.*;

public class XFileTest {

    private static RandomAccessFile _raFile;

    public static void main(String[] args) {
        final String[] fileNames = new String[10];
        final String[] fileNames2 = new String[10];
        final String[] ops = new String[10];
        int opCount = 0;
        boolean echo = false;

        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg.equals("f")) {
                fileNames[opCount] = args[++i];
            } else if (arg.equals("f2")) {
                fileNames2[opCount] = args[++i];
            } else if (arg.equals("op")) {
                ops[opCount++] = args[++i];
                fileNames[opCount] = fileNames[opCount - 1];
            } else if (arg.equals("echo")) {
                echo = true;
            }
        }

        for (int j = 0; j < opCount; j++) {
            try {
                final String fileName = fileNames[j];
                final XFile file = new XFile(fileName);
                final String op = ops[j];
                if (echo) {
                    System.out.println("command: " + op + " " + fileName);
                }
                if (op.equals("canRead")) {
                    System.out.println("canRead " + fileName + " returned "
                            + file.canRead());
                } else if (op.equals("canWrite")) {
                    System.out.println("canWrite " + fileName + " returned "
                            + file.canWrite());
/*
                } else if (op.equals("canExecute")) {
                    System.out.println("canExecute " + fileName + " returned "
                            + file.canExecute());
*/
                } else if (op.equals("exists")) {
                    System.out.println("exists " + fileName + " returned "
                            + file.exists());
                } else if (op.equals("isFile")) {
                    System.out.println("isFile " + fileName + " returned "
                            + file.isFile());
                } else if (op.equals("isDirectory")) {
                    System.out.println("isDirectory " + fileName + " returned "
                            + file.isDirectory());
                } else if (op.equals("getLength")) {
                    System.out.println("length of " + fileName + " is "
                            + file.length());
/*
                } else if (op.equals("setReadOnly")) {
                    System.out.println("setReadOnly " + fileName + " returned "
                            + file.setReadOnly());


                    } else if (op.equals("setWritable")) {
                    System.out.println("setWritable " + fileName + " returned "
                            + file.setWritable(true));
                } else if (op.equals("setExecutable")) {
                    System.out.println("setExecutable " + fileName
                            + " returned " + file.setExecutable(true));
                } else if (op.equals("unsetExecutable")) {
                    System.out.println("unsetExecutable " + fileName
                            + " returned " + file.setExecutable(false));
*/
                } else if (op.equals("list")) {
                    String[] files = file.list();
                    if (files == null) {
                        System.out.println("list returned null");
                    } else {
                        System.out.println("Contents of " + fileName);
                        for (String f : files) {
                            System.out.println("  " + f);
                        }
                    }
                } else if (op.equals("delete")) {
                    boolean rc = file.delete();
                    System.out.println("file delete of " + fileName
                            + checkRc(rc));
                } else if (op.equals("lastModified")) {
                    System.out.println("mtime of " + fileName + " is "
                            + file.lastModified());
                } else if (op.equals("isDirectory")) {
                    System.out.println("isDirectory of " + fileName + " is "
                            + file.isDirectory());
                } else if (op.equals("mkdir")) {
                    boolean rc = file.mkdir();
                    System.out.println("mkdir of " + fileName + checkRc(rc));
                } else if (op.equals("rename")) {
                    boolean rc = file.renameTo(new XFile(fileNames2[j]));
                    System.out.println("rename of " + fileName + " to "
                            + fileNames2[j] + checkRc(rc));
/*
                } else if (op.equals("createNewFile")) {
                    try {
                        final boolean rc = file.createNewFile();
                        System.out.println("createNewFile of " + fileName
                                + checkRc(rc));
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
*/
                } else if (op.equals("readFile")) {
                    readXFile(fileName, true);
                } else if (op.equals("readFileSingle")) {
                    readXFile(fileName, false);
                } else if (op.equals("writeFile")) {
                    writeXFile(fileName, false);
                } else {
                    System.out.println("unknown command: " + op);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    private static String checkRc(boolean rc) {
        return rc ? " ok" : " not ok";
    }

    private static void readXFile(String fileName, boolean array) {
        System.out.println("readFile  " + fileName + " " + (array ? "multiple" : "single"));
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(fileName);
            if (array) {
                int n;
                final byte[] buf = new byte[128];
                while ((n = fs.read(buf)) != -1) {
                    System.out.write(buf, 0, n);
                }
            } else {
                int b;
                while ((b = fs.read()) != -1) {
                    System.out.write(b);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private static void writeXFile(String fileName, boolean array) {
        System.out.println("writeFile  " + fileName + " "
                + (array ? "multiple" : "single"));
        final String data = "The Quick Brown Fox Jumps Over The Lazy Dog\n";
        final byte[] byteData = data.getBytes();
        final int dataLength = data.length();
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(fileName);
            for (int i = 0; i < 100; i++) {
                if (array) {
                    fs.write(byteData);
                } else {
                    for (int j = 0; j < dataLength; j++) {
                        fs.write(data.charAt(j));
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (Exception ex) {
                }
            }
        }

    }


}
