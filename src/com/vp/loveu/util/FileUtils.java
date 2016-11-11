package com.vp.loveu.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.vp.loveu.comm.VpConstants;

public class FileUtils {
	/**
	 * 初始化工程资源目录
	 */
	public static boolean initDir() {
		return createHomeDir();
	}

	/**
	 * 创建工程资源目录
	 */
	private static boolean createHomeDir() {
		File file = new File(VpConstants.HOME_DIR);
		if (!file.exists()) {
			return file.mkdirs();
		}
		return true;
	}

	/**
	 * 
	 * @Title: getFolderFilePaths
	 * @Description: 获取当前路径下的所有文件
	 * @param path
	 *            当前路径
	 * @return List<String>
	 * @throws @author
	 *             jie
	 * @date 2013-5-21 下午5:42:46
	 * @History: author time version desc
	 */
	public static List<File> getFolderFiles(String path) {
		List<File> files = new ArrayList<File>();
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			File[] temps = file.listFiles();
			for (File temp : temps) {
				files.add(temp);
			}
		}
		return files;
	}

	/**
	 * 
	 * @Title: copy
	 * @Description: 文件拷贝
	 * @param from
	 *            原始路径
	 * @param to
	 *            目标路径
	 * @return void
	 * @throws @author
	 *             jie
	 * @date 2013-5-30 下午1:58:31
	 * @History: author time version desc
	 */
	public static void copy(String from, String to) {
		try {
			InputStream in = new FileInputStream(from);
			OutputStream out = new FileOutputStream(to);

			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = in.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: moveTo
	 * @Description: 将文件夹下的内容移动到目标文件夹下
	 * @param @param
	 *            dirFromPath
	 * @param @param
	 *            dirToPath
	 * @return void
	 * @throws @author
	 *             jie
	 * @date 2013-5-30 下午1:56:33
	 * @History: author time version desc
	 */
	public static void moveTo(String dirFromPath, String dirToPath) {
		File dirFrom = new File(dirFromPath);
		File dirTo = new File(dirToPath);
		if (!dirTo.exists()) {
			dirTo.mkdirs();
		}
		File[] files = dirFrom.listFiles();
		for (File f : files) {
			String tempfrom = f.getAbsolutePath();
			String tempto = tempfrom.replace(dirFrom.getAbsolutePath(), dirTo.getAbsolutePath()); // 后面的路径
																									// 替换前面的路径名
			if (f.isDirectory()) {
				File tempFile = new File(tempto);
				tempFile.mkdirs();
				moveTo(tempfrom, tempto);
			} else {
				int endindex = tempto.lastIndexOf(File.separator);// 找到"/"所在的位置
				String mkdirPath = tempto.substring(0, endindex);
				File tempFile = new File(mkdirPath);
				tempFile.mkdirs();// 创建立文件夹
				FileUtils.copy(tempfrom, tempto);
			}
		}
		deleteDir(dirFrom);
	}

	/**
	 * 
	 * @Title: deleteDir
	 * @Description: 删除文件夹文件
	 * @param @param
	 *            dir
	 * @return void
	 * @throws @author
	 *             jie
	 * @date 2013-5-30 下午1:54:28
	 * @History: author time version desc
	 */
	public static void deleteDir(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return; // 检查参数
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(file); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	/**
	 * 
	 * @Title: readtxt @Description: 读取文件 @param @param
	 * filepath @param @return @param @throws Exception 设定文件 @return String
	 * 返回类型 @throws
	 */
	public static String readtxt(String filepath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		String str = "";
		String r = br.readLine();
		while (r != null) {
			str += r;
			r = br.readLine();
		}
		return str;
	}

	/**
	 * 
	 * @Title: writer @Description: 存储文件 @param @param filepath @param @param
	 * content @param @param filename @param @throws Exception 设定文件 @return void
	 * 返回类型 @throws
	 */
	public static void writer(String filepath, String content, String filename) throws Exception {
		File mDirectory = new File(filepath);
		if (!mDirectory.exists())
			mDirectory.mkdirs();
		File file = new File(filepath + filename);
		if (!file.exists())
			file.createNewFile();
		BufferedWriter mBufferedWriter = new BufferedWriter(new FileWriter(file));
		mBufferedWriter.write(content);
		mBufferedWriter.close();
	}

	/**
	 * 
	 * @Title: writeinfo @Description: 写入内容到程序内部文件 @param @param info
	 * 内容 @param @param filename 文件名 @param @param mContext 设定文件 @return void
	 * 返回类型 @throws
	 */
	@SuppressLint({ "WorldWriteableFiles", "WorldReadableFiles" })
	public static void writeinfo(String info, String filename, Context mContext) {
		FileOutputStream outStream = null;
		try {
			outStream = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
			byte[] buffer = info.getBytes("UTF-8");
			outStream.write(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.flush();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @Title: readinfo @Description: 读取程序内部文件 @param @param filename
	 * 文件名 @param @param mContext @param @return 设定文件 @return String
	 * 返回类型 @throws
	 */
	public static String readinfo(String filename, Context mContext) {
		String info = null;
		try {
			FileInputStream inStream = mContext.openFileInput(filename);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				stream.write(buffer, 0, length);
			}
			stream.close();
			inStream.close();
			info = stream.toString();
		} catch (Exception e) {
			return info;
		}
		return info;
	}

	public static InputStream getlh1(Context context, String filename) {
		try {
			return context.getAssets().open(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray getInfo(AssetManager asset, String filename) {
		InputStream is;
		try {
			is = asset.open(filename);
			byte[] byte_lh = new byte[is.available()];
			is.read(byte_lh);
			return new JSONArray(new String(byte_lh, "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray getLocationArae(AssetManager asset) {
		InputStream is;
		try {
			is = asset.open("smallAre.txt");
			byte[] byte_lh = new byte[is.available()];
			is.read(byte_lh);
			JSONArray jsonObj = new JSONArray(new String(byte_lh, "utf-8"));
			return jsonObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @Title: getArea @Description:获得城市 @param @param asset @param @return
	 * 设定文件 @return JSONArray 返回类型 @throws
	 */
	public static JSONArray getArea(AssetManager asset) { // TODO OOM 待优化
		InputStream is;
		try {
			is = asset.open("areas.json");
			byte[] byte_lh = new byte[is.available()];
			is.read(byte_lh);
			JSONObject json = new JSONObject(new String(byte_lh, "utf-8"));
			return json.getJSONArray("all");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray getRegion(AssetManager asset) {
		InputStream is;
		try {
			is = asset.open("region.json");
			byte[] byte_lh = new byte[is.available()];
			is.read(byte_lh);
			JSONObject jsonObj = new JSONObject(new String(byte_lh, "utf-8"));
			return jsonObj.getJSONArray("all");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// public static JSONArray getURBAN_RENEWAL(AssetManager asset) {
	// InputStream is;
	// try {
	// is = asset.open("lh");
	// byte[] byte_lh = new byte[is.available()];
	// is.read(byte_lh);
	// return new JSONObject(new String(byte_lh,
	// "utf-8")).getJSONArray(VpConstants.JsonKey.URBAN_RENEWAL);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//

	/**
	 * 
	 * @Title: @Description: TODO(这里用一句话描述这个方法的作用) @param @param
	 * context @param @param fileName 在assets中的文件名字 @param @param SDFileName
	 * 在SD卡中的文件名字 @return void 返回类型 @throws
	 */
	public void aaaaa(Context context, String fileName, String SDFileName) {
		try {
			String filepath = Environment.getExternalStorageDirectory() + "/" + SDFileName;
			if (!(new File(filepath)).exists()) {
				new File(filepath).createNewFile();
				InputStream is = context.getAssets().open(fileName);
				FileOutputStream fos = new FileOutputStream(filepath);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param context
	 * @return
	 */
	public static List<String> getEmojiFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("emoji");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
