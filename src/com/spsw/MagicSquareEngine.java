package com.spsw;

public class MagicSquareEngine {

	private int[] totals = new int[32000];
	private int[] numAry = new int[9];
    private int[][] numSquare = new int[3][3];
	private int debugLevel = -1;
	private int sampleTotal = -1;
	private int loopLimit = 320000;
	private boolean bShowTotals = false; 
	private boolean bNoLimit = false; 
	private boolean bUseLaytonSeed = false; 

	private ArgParser ap;
	private QRandom re; 

	public MagicSquareEngine(String[] args) { 
		ap = new ArgParser(args);
		re = new QRandom(); 

	}

	public static void main(String[] args) {

		MagicSquareEngine mse = new MagicSquareEngine(args);
		mse.go();

	}

	public void go() {

		// Note: argParser was populated w/ args in constructor.
		if(!parseArgs()) {
			showUsage();
			return; 
		}

		boolean bMatchFound = false; 
		int lc = 0;
		do {
			lc++;
			numAry = getRandArray();
			populateSquare();
			bMatchFound = checkTotals(); 

			showProgress(lc);
			showSampleSquare();

		} while (!bMatchFound && (isUnderLoopLimit(lc)));

		if (bMatchFound) {
			showMsg(String.format("Found it on loop %d!\n", lc), 1);
			showSquare();
		}
		else {
			showMsg(String.format("Loop limit hit without a match, used %d totals and %d loops\n", totals[0], lc), 1);
			if (bShowTotals) 
				showTotals();
		}

	}

	public boolean isUnderLoopLimit(int lc) {
		boolean bRval = true; 

		if (!bNoLimit && (lc >= loopLimit))
			bRval = false;

		return bRval; 

	}

	public boolean parseArgs() {

		boolean bRval = true; 

		if (ap.isInArgs("-debuglevel", true))
			debugLevel = Integer.parseInt(ap.getArgValue("-debuglevel"));

		if (ap.isInArgs("-sampletotal", true))
			sampleTotal = Integer.parseInt(ap.getArgValue("-sampletotal"));

		if (ap.isInArgs("-looplimit", true))
			loopLimit = Integer.parseInt(ap.getArgValue("-looplimit"));

		if (ap.isInArgs("-showtotals", false))
			bShowTotals = true;

		if (ap.isInArgs("-nolimit", false))
			bNoLimit = true;

		if (ap.isInArgs("-laytonseed", false))
			bUseLaytonSeed = true;

		

		// TODO: parseArgs() not fully implemented. 

		return bRval; 
	}

	private void showUsage() {

		// TODO: showUsage() not yet implemented. 

	}

	public int[] getRandArray() {
		int[] rval = new int[9];

		// seed the array with values for Professor Layton 
		if(bUseLaytonSeed) {
			rval[0] = 2;
			rval[7] = 1;
		}

		for (int i=0; i<9; i++) {
			int nc = re.Next(1, 9);
			while(isInArray(nc, rval))
				nc = re.Next(1, 9); 

			if (rval[i] == 0)
				rval[i] = nc;

		}

		return rval;

	}

	public void populateSquare() {

		int i=0;
		for (int y=0; y<3; y++) 
			for (int x=0; x<3; x++) {
				this.numSquare[x][y] = numAry[i];
				i++;
			}

		}

	public boolean checkTotals() {
		boolean bTotalsMatch = true; 

		int sqtotals[] = new int[8];
		int wt = 0; // working total
		int targetTot = 0; 
		int ttIdx = 0; 

		// Set targetTotal to first row total
		for (int x=0; x<3; x++) 
			targetTot += numSquare[x][0];
		sqtotals[ttIdx++] = targetTot;

		// add to totals used
		boolean bTotalFound = false;
		int usedTotals = totals[0];
		for (int i=1; i<=usedTotals; i++) {
			if (targetTot == totals[i]) {
				bTotalFound = true; 
				break;
			}
		}

		if (!bTotalFound) {
			totals[0] = usedTotals+1;
			totals[usedTotals+1] = targetTot;
		}

		// check rest of rows 
		for (int y=1; y<3; y++) {
			for (int x=0; x<3; x++) 
				wt += numSquare[x][y];

			if (wt != targetTot) {
				bTotalsMatch = false;
			}

			sqtotals[ttIdx++] = wt;
			wt = 0;
		}

		// check columns 
		wt = 0;
		for (int x=0; x<3; x++) {
			for (int y=0; y<3; y++) 
				wt += numSquare[x][y];

			if (wt != targetTot) {
				bTotalsMatch = false;
			}

			sqtotals[ttIdx++] = wt;
			wt = 0;

		}

		// check diagonals 
		// 1 of 2: Top Left to Bottom Right 
		wt = 0;
		for (int x=0; x<3; x++) 
			wt += numSquare[x][x];
		sqtotals[ttIdx++] = wt;

		if (wt != targetTot) 
			bTotalsMatch = false;
		
		// 2 of 2: Bottom Left to Top Right 
		wt = 0;
		int y = 2;
		for (int x=0; x<3; x++) {
			wt += numSquare[x][y];
			y--;
		}
		sqtotals[ttIdx++] = wt;

		if (wt != targetTot) 
			bTotalsMatch = false;

		if (bTotalsMatch) {
			// show square totals 
			showMsg("Found square with totals matching, total values were: ", 1);
			for (int i=0; i<8; i++) 
				showMsg(String.format(" %d", sqtotals[i]), 1); 
			showMsg("\n", 1);

		}

		return bTotalsMatch;

	}

	private boolean isInArray(int nc, int[] ary) {
		boolean bRval = false;

		for (int i=0; i<9; i++) 
			if(nc == ary[i]) {
				bRval = true;
				break; 
			}

		return bRval; 

	}

	private void showSquare() {

		showMsg(" -----\n", 1);
		for (int y=0; y<3; y++) {
			for(int x=0; x<3; x++)
				showMsg(String.format(" %d", numSquare[x][y]), 1);

			showMsg("\n", 1); 
		}
		showMsg(" -----\n", 1);

	}

	private void showSampleSquare() {


		// Get total for first row
		int total = 0;
		for (int x=0; x<3; x++) 
			total += numSquare[x][0];

		if (total == sampleTotal) 
			showSquare();

	}

	private void showTotals() {

		for (int i=1; i<=totals[0]; i++) {
			showMsg(String.format("%d ", totals[i]), 1);

		}
		showMsg("\n", 1);

	}

	private void showProgress(int lc) { 

		if ((lc % 500000) == 0) {
			showMsg(".", 1);

		} 

	}

	private void showMsg(String msg, int msgLevel) {

		if (msgLevel >= debugLevel) 
			System.out.print(msg); 

	}

}