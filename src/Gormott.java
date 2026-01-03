
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import GormottEngine.GlEngine;

public class Gormott {
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		// try {
		// 	TimeUnit.SECONDS.sleep(15);
		// } catch (InterruptedException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }

		new Thread(){
			public void run(){
				new GlEngine(800, 600, "Gormott", "src\\html-css\\ah.png").run();
			}
		}.start();

		int testnb = 1;
		for (int i = 0; i < testnb; i++) {
			
			long mtn = System.nanoTime();
			DocumentObjModel DOM = new DocumentObjModel("src\\html-css\\index.html");
			long fini = System.nanoTime();
			System.out.println("temps dexecution de algo " + ((fini - mtn)/1000)+" micro secondes");
		}
	}

}