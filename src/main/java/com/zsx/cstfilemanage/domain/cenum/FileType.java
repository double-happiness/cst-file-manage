package com.zsx.cstfilemanage.domain.cenum;

/**
 * 文件类型枚举
 */
public enum FileType {
    CAD_DWG("CAD图纸", ".dwg"),
    CAD_DXF("CAD图纸", ".dxf"),
    PDF("PDF文档", ".pdf"),
    JPEG("图片", ".jpeg,.jpg"),
    PNG("图片", ".png"),
    WORD("Word文档", ".doc,.docx"),
    EXCEL("Excel文档", ".xls,.xlsx"),
    OTHER("其他", "");

    private final String description;
    private final String extensions;

    FileType(String description, String extensions) {
        this.description = description;
        this.extensions = extensions;
    }

    public String getDescription() {
        return description;
    }

    public String getExtensions() {
        return extensions;
    }

    public static FileType fromExtension(String extension) {
        if (extension == null) {
            return OTHER;
        }
        String ext = extension.toLowerCase();
        for (FileType type : values()) {
            if (type.extensions.contains(ext)) {
                return type;
            }
        }
        return OTHER;
    }
}

