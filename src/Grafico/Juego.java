package Grafico;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Juego extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	private static final int ALTURA = 600;
	private static final int ANCHURA = 800;
	
	private static final String NOMBRE = "GWORD";

	private static int ACTUALIZACION_POR_SEGUNDO = 0;
	private static int FRAMES_POR_SEGUNDO = 0;
	
	private static volatile boolean estaEjecutado = false;
	
	private static JFrame Ventana;
	private static Thread Hilo;
	
	private Juego(){
		setPreferredSize(new Dimension(ANCHURA, ALTURA));
		Ventana = new JFrame(NOMBRE);
		Ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Ventana.setResizable(false);
		Ventana.setLayout(new BorderLayout());
		Ventana.setLocationRelativeTo(null);
		Ventana.add(this, BorderLayout.CENTER);
		Ventana.pack();
		Ventana.setVisible(true);
	}
	
	public static void main(String[] args){
		Juego gword = new Juego();
		gword.Iniciar();
	}

	/* synchronized para que no se ejecute de manera simultanea y colapse la variable estaEjecutado */
	private synchronized void Iniciar(){
		estaEjecutado = true;
		Hilo = new Thread(this, "Grafico");
		Hilo.start();
	}
	
	private synchronized void Detener(){
		estaEjecutado = false;
		
		/* Join detiene de manera no "bruta" el hilo a excepción de stop */
		try {
			Hilo.join();
		}catch (InterruptedException e){
			
			/* muestra el error, simple excepción */
			e.printStackTrace();
		}
	}
	
	public void run(){
		final int NS_POR_SEGUNDO = 1000000000;
		final byte ACTUALIZACIONES_POR_SEGUNDO = 60;
		final double NS_POR_ACTUALIZACION = NS_POR_SEGUNDO / ACTUALIZACIONES_POR_SEGUNDO;
		
		long punteroActualizacion = System.nanoTime();
		long punteroContador = System.nanoTime();
		double tiempoTranscurrido;
		
		/* Cantidad de tiempo que ha transcurrido hasta que se realiza una actualizacion */
		double Delta = 0;
		
		while(estaEjecutado){
			final long inicioBucle = System.nanoTime();
			tiempoTranscurrido = inicioBucle - punteroActualizacion;
			punteroActualizacion = inicioBucle;
			Delta += tiempoTranscurrido / NS_POR_ACTUALIZACION;
			
			while(Delta >= 1) {
				Actualizar();
				Delta--;
			}
			Dibujar();
			if((System.nanoTime() - punteroContador) > NS_POR_SEGUNDO) {
				Ventana.setTitle(NOMBRE + " APS:" + ACTUALIZACION_POR_SEGUNDO + " FPS:" + FRAMES_POR_SEGUNDO);
				ACTUALIZACION_POR_SEGUNDO = 0;
				FRAMES_POR_SEGUNDO = 0;
				punteroContador = System.nanoTime();
			}
		}
	}
	
	private void Dibujar(){
		FRAMES_POR_SEGUNDO++;
	}
	
	private void Actualizar(){
		ACTUALIZACION_POR_SEGUNDO++;
	}
}
