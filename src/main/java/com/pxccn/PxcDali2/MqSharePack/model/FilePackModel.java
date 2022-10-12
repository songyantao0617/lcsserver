package com.pxccn.PxcDali2.MqSharePack.model;

import com.google.protobuf.ByteString;
import com.pxccn.PxcDali2.Proto.LcsProtos;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

public class FilePackModel implements IPbModel<LcsProtos.FilePack> {

    public static FilePackModel FromFile(String filePath) throws IOException {
        File f = new File(filePath);
        try (FileInputStream fis = new FileInputStream(f)) {
            String name = f.getName();
            byte[] bytes = ReadAll(fis,Integer.MAX_VALUE);
            CRC32 crc32 = new CRC32();
            crc32.update(bytes);
            long crc = crc32.getValue();
            return new FilePackModel(name,crc,ByteString.copyFrom(bytes));
        }
    }


    public void TransTo(String targetPath) throws IOException {
        byte[] content = this.content.toByteArray();
        CRC32 crc32 = new CRC32();
        crc32.update(content);
        long crc = crc32.getValue();
        if (crc!=this.crc32){
            throw new IOException("Bad CRC");
        }
        File file = new File(targetPath+File.separator+this.fileName);
        boolean exist = createMissingParentDirectories(file);
        if(! exist){
            throw new IOException("Bad target path");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(content);
        fileOutputStream.close();
    }


    private static byte[] ReadAll(FileInputStream fi,int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, 8192)];
            int nread = 0;

            // read to EOF which may read more or less than buffer size
            while ((n = fi.read(buf, nread,Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }
            if (nread > 0) {
                if (Integer.MAX_VALUE - 8 - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }
        return result;
    }


    static public boolean createMissingParentDirectories(File file) {
        File parent = file.getParentFile();
        if (parent == null) {
            return true;
        }
        parent.mkdirs();
        return parent.exists();
    }


    /*
    message FilePack{
  string fileName = 1;
  int64 crc32 = 2;
  bytes content = 3;
}
     */

    public String getFileName() {
        return fileName;
    }

    public long getCrc32() {
        return crc32;
    }

    public ByteString getContent() {
        return content;
    }

    final String fileName;
    final long crc32;
    final ByteString content;

    public FilePackModel(
            String fileName,
            long crc32,
            ByteString content
    ) {
        this.fileName = fileName;
        this.crc32 = crc32;
        this.content = content;
    }

    public FilePackModel(LcsProtos.FilePack pb) {
        this.fileName = pb.getFileName();
        this.crc32 = pb.getCrc32();
        this.content = pb.getContent();
    }



    public LcsProtos.FilePack getPb() {
        return LcsProtos.FilePack.newBuilder()
                .setFileName(this.fileName)
                .setCrc32(this.crc32)
                .setContent(this.content)
                .build();
    }
}
