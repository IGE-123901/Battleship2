package battleship;

public class GameStatistics {
    Integer countInvalidShots;

    public Integer getCountInvalidShots() {
        return countInvalidShots;
    }

    public void setCountInvalidShots(Integer countInvalidShots) {
        this.countInvalidShots = countInvalidShots;
    }

    Integer countRepeatedShots;

    public Integer getCountRepeatedShots() {
        return countRepeatedShots;
    }

    public void setCountRepeatedShots(Integer countRepeatedShots) {
        this.countRepeatedShots = countRepeatedShots;
    }

    Integer countHits;

    public Integer getCountHits() {
        return countHits;
    }

    public void setCountHits(Integer countHits) {
        this.countHits = countHits;
    }

    Integer countSinks;

    public Integer getCountSinks() {
        return countSinks;
    }

    public void setCountSinks(Integer countSinks) {
        this.countSinks = countSinks;
    }

    public GameStatistics() {
    }

    public int getRepeatedShots() {
        return this.countRepeatedShots;
    }

    public int getInvalidShots() {
        return this.countInvalidShots;
    }

    public int getHits() {
        return this.countHits;
    }

    public int getSunkShips() {
        return this.countSinks;
    }
}