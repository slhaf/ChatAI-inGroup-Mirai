package plugin.pojo;

import java.util.List;

public class OCRDataInfo {

    /**
     * algo_version
     */
    private String algo_version;
    /**
     * 文字内容-段落
     */
    private String content;
    /**
     * height
     */
    private int height;
    /**
     * orgHeight
     */
    private int orgHeight;
    /**
     * orgWidth
     */
    private int orgWidth;
    /**
     * prism_version
     */
    private String prism_version;
    /**
     * prism_wnum
     */
    private int prism_wnum;
    /**
     * prism_wordsInfo
     */
    private List<PrismWordsInfoBean> prism_wordsInfo;
    /**
     * width
     */
    private int width;

    public String getAlgo_version() {
        return algo_version;
    }

    public void setAlgo_version(String algo_version) {
        this.algo_version = algo_version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getOrgHeight() {
        return orgHeight;
    }

    public void setOrgHeight(int orgHeight) {
        this.orgHeight = orgHeight;
    }

    public int getOrgWidth() {
        return orgWidth;
    }

    public void setOrgWidth(int orgWidth) {
        this.orgWidth = orgWidth;
    }

    public String getPrism_version() {
        return prism_version;
    }

    public void setPrism_version(String prism_version) {
        this.prism_version = prism_version;
    }

    public int getPrism_wnum() {
        return prism_wnum;
    }

    public void setPrism_wnum(int prism_wnum) {
        this.prism_wnum = prism_wnum;
    }

    public List<PrismWordsInfoBean> getPrism_wordsInfo() {
        return prism_wordsInfo;
    }

    public void setPrism_wordsInfo(List<PrismWordsInfoBean> prism_wordsInfo) {
        this.prism_wordsInfo = prism_wordsInfo;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public static class PrismWordsInfoBean {
        /**
         * angle
         */
        private int angle;
        /**
         * direction
         */
        private int direction;
        /**
         * height
         */
        private int height;
        /**
         * pos
         */
        private List<PosBean> pos;
        /**
         * prob
         */
        private int prob;
        /**
         * width
         */
        private int width;
        /**
         * 文字内容-行
         */
        private String word;
        /**
         * x
         */
        private int x;
        /**
         * y
         */
        private int y;

        public int getAngle() {
            return angle;
        }

        public void setAngle(int angle) {
            this.angle = angle;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public List<PosBean> getPos() {
            return pos;
        }

        public void setPos(List<PosBean> pos) {
            this.pos = pos;
        }

        public int getProb() {
            return prob;
        }

        public void setProb(int prob) {
            this.prob = prob;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public static class PosBean {
            /**
             * x
             */
            private int x;
            /**
             * y
             */
            private int y;

            public int getX() {
                return x;
            }

            public void setX(int x) {
                this.x = x;
            }

            public int getY() {
                return y;
            }

            public void setY(int y) {
                this.y = y;
            }
        }
    }
}
