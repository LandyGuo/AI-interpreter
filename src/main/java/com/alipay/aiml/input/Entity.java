package com.alipay.aiml.input;

public class Entity {
	   private static final long serialVersionUID = -935676608915565672L;
	    /**
	     * The Content.
	     */
	    private String content;
	    /**
	     * The Type.
	     */
	    private String type;
	    /**
	     * The Start.
	     */
	    private int start;
	    /**
	     * The End.
	     */
	    private int end;

	    /**
	     * Instantiates a new Info entity.
	     *
	     * @param content the content
	     * @param type    the type
	     * @param start   the start
	     * @param end     the end
	     */
	    public Entity(String content, String type, int start, int end) {
	        this.content = content;
	        this.type = type;
	        this.start = start;
	        this.end = end;
	    }
	    
	    /**
	     * Append entity.
	     *
	     * @param other the other
	     * @return the entity
	     */
	    public Entity append(Entity other) {
	        if (this.end != other.start) {
	            return null;
	        }
	        this.content += other.content;
	        this.end = other.end;
	        return this;
	    }


	    /**
	     * Gets content.
	     *
	     * @return the content
	     */
	    public String getContent() {
	        return content;
	    }

	    /**
	     * Sets content.
	     *
	     * @param content the content
	     */
	    public void setContent(String content) {
	        this.content = content;
	    }

	    /**
	     * Gets type.
	     *
	     * @return the type
	     */
	    public String getType() {
	        return type;
	    }

	    /**
	     * Sets type.
	     *
	     * @param type the type
	     */
	    public void setType(String type) {
	        this.type = type;
	    }

	    /**
	     * Gets start.
	     *
	     * @return the start
	     */
	    public int getStart() {
	        return start;
	    }

	    /**
	     * Sets start.
	     *
	     * @param start the start
	     */
	    public void setStart(int start) {
	        this.start = start;
	    }

	    /**
	     * Gets end.
	     *
	     * @return the end
	     */
	    public int getEnd() {
	        return end;
	    }

	    /**
	     * Sets end.
	     *
	     * @param end the end
	     */
	    public void setEnd(int end) {
	        this.end = end;
	    }
}
