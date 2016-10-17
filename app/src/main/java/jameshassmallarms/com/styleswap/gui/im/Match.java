package jameshassmallarms.com.styleswap.gui.im;

/**
 * Created by gary on 13/10/16.
 */

public class Match {
        private String matchName;
        private int matchImage; //Needs refactoring
        private String matchNumber;

        public Match() {

        }

        public String getMatchName() {
                return matchName;
        }

        public void setMatchName(String matchName) {
                this.matchName = matchName;
        }

        public int getMatchImage() {
                return matchImage;
        }

        public void setMatchImage(int matchImage) {
                this.matchImage = matchImage;
        }

        public String getMatchNumber() {
                return matchNumber;
        }

        public void setMatchNumber(String matchNumber) {
                this.matchNumber = matchNumber;
        }
}
