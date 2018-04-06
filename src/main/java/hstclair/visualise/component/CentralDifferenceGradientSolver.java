package hstclair.visualise.component;

public class CentralDifferenceGradientSolver {

    final static int dimensions = 2;

    int edgeLength;
    int rowLength;
    int startRowIndex;
    int bufferSize;
    int bufferSizeX2;

    public CentralDifferenceGradientSolver(int edgeLength, int bufferSize) {
        this.edgeLength = edgeLength;
        this.startRowIndex = (edgeLength + bufferSize) * bufferSize;
        this.bufferSize = bufferSize;
        this.bufferSizeX2 = bufferSize << 1;
        this.rowLength = edgeLength + bufferSizeX2;
    }


    public void computeGradient(double[] gradientX, double[] gradientY, double[] scalar) {

        int index = startRowIndex + bufferSize;

        for (int row = 0; row < edgeLength; row++) {

            for (int col = 0; col < edgeLength; col++, index++) {
                gradientX[index] += scalar[index + 1] - scalar[index - 1];
                gradientY[index] += scalar[index + rowLength] - scalar[index - rowLength];
            }
        }
    }
}
