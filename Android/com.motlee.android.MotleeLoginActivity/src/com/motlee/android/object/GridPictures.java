package com.motlee.android.object;

public class GridPictures {

	public static final PhotoItem NO_PICTURE = null;
	
	public PhotoItem image1 = NO_PICTURE;
	public PhotoItem image2 = NO_PICTURE;
	public PhotoItem image3 = NO_PICTURE;
	
	public GridPictures() {
		// TODO Auto-generated constructor stub
	}

	public GridPictures(PhotoItem image1, PhotoItem image2, PhotoItem image3) {
		this.image1 = image1;
		this.image2 = image2;
		this.image3 = image3;
	}
	
	public int hashCode() {
        Integer hashCode = 3;
        if (image1 != null)
        {
        	hashCode = hashCode + image1.id;
        }
        if (image2 != null)
        {
        	hashCode = hashCode + image2.id;
        }
        if (image3 != null)
        {
        	hashCode = hashCode + image3.id;
        }
        return hashCode;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GridPictures))
            return false;

        GridPictures rhs = (GridPictures) obj;
        
        return (rhs.image1 == this.image1) && (rhs.image2 == this.image2) && (rhs.image3 == this.image3);
    }
}
