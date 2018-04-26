package com.waldo.test.ImageSocketServer;

import java.awt.*;

public enum ImageType {
    Other(0,            new Dimension(512, 512), "Others"),
    ItemImage(1,        new Dimension(256, 256), "ItemImages"),
    DistributorImage(2, new Dimension(256, 256), "DistributorImage"),
    DivisionImage(3,    new Dimension(256, 256), "DivisionImage"),
    IdeImage(4,         new Dimension(256, 256), "IdeImage"),
    ManufacturerImage(5,new Dimension(256, 256), "ManufacturerImage"),
    ProjectImage(6,     new Dimension(256, 256), "ProjectImage");

    private final int id;
    private final Dimension dimension;
    private final String folderName;

    ImageType(int id, Dimension dimension, String folderName) {
        this.id = id;
        this.dimension = dimension;
        this.folderName = folderName;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public int getId() {
        return id;
    }

    public String getFolderName() {
        return folderName;
    }

    public static ImageType fromInt(int id) {
        switch (id) {
            default: return Other;
            case 1:  return ItemImage;
            case 2:  return DistributorImage;
            case 3:  return DivisionImage;
            case 4:  return IdeImage;
            case 5:  return ManufacturerImage;
            case 6:  return ProjectImage;
        }
    }
}
