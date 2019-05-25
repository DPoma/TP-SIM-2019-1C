package test;

import java.text.NumberFormat;

public class Simulacion 
{
	private static final int HV = 99999;
	private static final int N = 1;

	private static int[] TPSCF = new int[N];
	private static int[] ITO = new int[N];
	private static double[] STO = new double[N];
	private static double[] PTO = new double[N];
	private static boolean[] CFT = new boolean[N];
	private static double STACT = 0;
	private static double PTCT = 0;
	private static int T = 0;
	private static int NS = 0; 
	private static int TPLL = 0;
	private static int TPSCT = HV;
	private static int TF = 60000;
	private static int i = 0;
	private static boolean CTA = false;

	public static void main(String[] args) 
	{
		iniciarVariables();
		do
		{
			do 
			{
				i = menorTPS();
				if(TPSCF[i] <= TPSCT)
				{
					if (TPLL <= TPSCF[i]) 
						llegadaCliente();
					else
						salidaCajaFija();
				}
				else
				{
					if (TPLL <= TPSCT) 
						llegadaCliente();
					else
						salidaCajaTemporal();
				}
			} 
			while (T <= TF);
			
			if (NS > 0)
				TPLL = HV;
		} 
		while (NS > 0);
		
		calcularResultados();
		imprimirResultados();
	}

	private static void llegadaCliente() {
		T = TPLL;
		int IA = IA();
		TPLL = T + IA;
		NS = NS + 1;
		if (CTA == true) 
		{
			if (NS <= N + 1)
			{
				i = buscarCaja();
				STO[i] = STO[i] + T - ITO[i];
				int TA = TA();
				TPSCF[i] = T + TA;
			}
		}
		else
		{
			if (NS <= N) 
			{
				i = buscarCaja();
				STO[i] = STO[i] + T - ITO[i];
				int TA = TA();
				TPSCF[i] = T + TA;
			} 
			else 
			{
				if (NS == N + 6) 
				{
					CTA = true;
					int TA = TA();
					TPSCT = T + TA;
					STACT = STACT + TA;
				}
			}
		}
	}
	
	private static void salidaCajaFija() {
		T = TPSCF[i];
		NS = NS - 1;
		if (CTA == true)
		{
			if (NS >= N + 1) 
			{
				int TA = TA();
				TPSCF[i] = T + TA;
			} 
			else 
			{
				ITO[i] = T;
				TPSCF[i] = HV;
			}
		}
		else
		{
			if (NS >= N) 
			{
				int TA = TA();
				TPSCF[i] = T + TA;
			} 
			else 
			{
				ITO[i] = T;
				TPSCF[i] = HV;
			}
		}	
	}
	
	private static void salidaCajaTemporal()
	{
		T = TPSCT;
		NS = NS - 1;
		if (NS > N + 5) 
		{
			int TA = TA();
			TPSCT = T + TA;
			STACT = STACT + TA;
		}
		else
		{
			CTA = false;
			TPSCT = HV;
		}
	}
	
	private static void iniciarVariables()
	{
		for (int i = 0; i < N; i++) 
		{
			STO[i] = 0;
			ITO[i] = 0;
			PTO[i] = 0;
			TPSCF[i] = HV;
			CFT[i] = false;
		}
	}
	
	private static void calcularResultados() 
	{
		PTCT = (STACT * 100) / T;
		for (int i = 0; i < N; i++)
			if(CFT[i])
				PTO[i] = (STO[i] * 100) / T;
			else
				PTO[i] = 100.0;
	}
	
	private static void imprimirResultados() 
	{
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		System.out.println("-------------------------------------------------------------------------");
		System.out.println("RESULTADOS DE LA SIMULACION");
		System.out.println("LOCAL DE ROPA CON " + N + " CAJAS FIJAS Y UNA CAJA TEMPORAL");
		for (int i = 0; i < N; i++)		
			System.out.println("Porcentaje de tiempo ocioso de la caja fija " + (i + 1) + " (PTO): " + numberFormat.format(PTO[i]) + "%");
		System.out.println("Porcentaje de tiempo trabajado por la caja temporal (PTCT): " + numberFormat.format(PTCT) + "%");
		System.out.println("-------------------------------------------------------------------------");
	}
	
	private static int buscarCaja() {
		int i;
		for (i = 0; i < N && TPSCF[i] != HV ; i++);
		CFT[i] = true;
		return i;
	}

	private static int menorTPS() {
		int i = 0;
		int j;
		for (j = 0; j < N; j++)
			if(TPSCF[j] < TPSCF[i])
				i = j;
		return i;
	}
	
	private static int IA() {
		double random = Math.random();
		if(random == 0.0 || random == 1.0)
			random = Math.random();
		int IA = (int) (Math.pow( (1 / Math.pow(((1 / random) - 1), (1/1.7209))), (1 / 1.9502)) * 5.3603);
		return IA;
	}

	private static int TA() {
		double random = Math.random();
		int TA = (int) ((Math.pow(random, (1 / 1.2098)) * (15 - 3.9794)) + 3.9794) ;
		return TA;
	}
}