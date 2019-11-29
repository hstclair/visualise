package hstclair.visualise.grid;

public class EightfoldSymmetryTraversalStrategyFactory implements TraversalStrategyFactory {

    Axis axis;
    boolean reflected;

    CoordReference limit;

    CoordReference recenter;

    Orientation advanceAxis;
    Orientation advanceOffAxis;

    public EightfoldSymmetryTraversalStrategyFactory(Axis axis, boolean reflected) {
        this.axis = axis;
        this.reflected = reflected;
    }

    @Override
    public TraversalStrategy create(Indexor indexor) {

        return new EightfoldSymmetryTraversalStrategy()
                .setParameters()
                .initIndexor(indexor);
    }

    public class EightfoldSymmetryTraversalStrategy implements TraversalStrategy {

        EightfoldSymmetryTraversalStrategy setParameters() {

            switch (axis) {
                case AscendingX:

                    recenter = CoordReference.CenterY;
                    advanceAxis = Orientation.Right;
                    advanceOffAxis = reflected ? Orientation.Down : Orientation.Up;
                    limit = reflected ? CoordReference.MaxXY : CoordReference.MaxXMinY;

                    break;

                case DescendingX:

                    recenter = CoordReference.CenterY;
                    advanceAxis = Orientation.Left;
                    advanceOffAxis = reflected ? Orientation.Down : Orientation.Up;
                    limit = reflected ? CoordReference.MinXMaxY : CoordReference.MinXY;

                    break;

                case AscendingY:

                    recenter = CoordReference.CenterX;
                    advanceAxis = Orientation.Down;
                    advanceOffAxis = reflected ? Orientation.Left : Orientation.Right;
                    limit = reflected ? CoordReference.MinXMaxY : CoordReference.MaxXY;

                    break;

                case DescendingY:

                    recenter = CoordReference.CenterX;
                    advanceAxis = Orientation.Up;
                    advanceOffAxis = reflected ? Orientation.Left : Orientation.Right;
                    limit = reflected ? CoordReference.MinXY : CoordReference.MaxXMinY;

                    break;

                default:

                    throw new IllegalStateException("Unknown axis");
            }

            return this;
        }

        @Override
        public TraversalStrategy initIndexor(Indexor indexor) {

            indexor.moveTo(CoordReference.CenterXY);

            return this;
        }

        @Override
        public boolean done(Indexor indexor) {

            return indexor.at(limit);
        }

        @Override
        public void advance(Indexor indexor) {
            if (indexor.x == indexor.y) {

                indexor.moveTo(recenter);
                indexor.move(advanceAxis);
            } else
                indexor.move(advanceOffAxis);
        }
    }
}
