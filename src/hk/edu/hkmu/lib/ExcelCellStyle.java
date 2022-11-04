package hk.edu.hkmu.lib;

/**
 * 
 * This class serves as a collection of Excel Styles used by Apache POI.
 * 
 * @author Wai-yan NG
 * @author wwyng@ouhk.edu.hk
 * @version 1.0
 * @since Feb 22, 2020
 */
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

public final class ExcelCellStyle {

	public static CellStyle defaultFontStyle;
	public static CellStyle defaultFontStyleWithoutBorder;
	public static CellStyle defaultFontStyleBoldWithoutBorder;
	public static CellStyle defaultFontStyleDateFormat;
	public static CellStyle defaultFontStyleBold;
	public static CellStyle defaultFontStyleCenter;
	public static CellStyle defaultFontStyleCenterBold;
	public static CellStyle defaultFontStyleCenterBoldBottomAlign;
	public static CellStyle defaultFontStyleRightAlign;
	public static CellStyle defaultFontStyleLeftAlign;
	public static CellStyle defaultFontStyleLeftAlignLIBSessionStyle;
	public static CellStyle defaultFontStyleSessionFirstLineLeftGrid;
	public static CellStyle defaultFontStyleSessionFirstLineRightGrid;
	public static CellStyle defaultFontStyleGreyFill;
	public static CellStyle defaultFontStyleGreyFillLeftGrid;
	public static CellStyle defaultFontStyleGreyFillRightGrid;
	public static CellStyle defaultFontStyleGreyFillSessionFirstLine;
	public static CellStyle defaultFontStyleGreyFillSessionFirstLineLeftGrid;
	public static CellStyle defaultFontStyleGreyFillSessionFirstLineRightGrid;
	public static CellStyle defaultFontStyleRightAlignBold;
	public static CellStyle defaultFontStyleWithoutBorderRightAlign;
	public static CellStyle defaultFontStyleSessionFirstLine;
	public static CellStyle defaultFontStyleCenterRightGrid;
	public static CellStyle defaultFontStyleCenterLeftGrid;

	public static CellStyle titleFontStyle;
	public static CellStyle titleFontStyleCenter;
	public static CellStyle titleFontStyleLeftAlign;
	public static CellStyle titleFontStyleCenterWithoutBorder;
	
	public static CellStyle titleFontStyleBigger;
	public static CellStyle titleFontStyleCenterBigger;
	public static CellStyle titleFontStyleLeftAlignBigger;
	public static CellStyle titleFontStyleCenterWithoutBorderBigger;

	private static Workbook workbook;
	private static Font defaultFont;
	private static Font defaultFontBold;
	private static Font titleFont;
	private static Font titleFontBigger;

	public static void init(Workbook workbook2) {
		workbook = workbook2;
		initDefaultFont();
		initDefaultFontBold();
		initTitleFont();
		initDefaultFontStyle();
		initTitleFontStyle();
	}

	private static void initDefaultFont() {
		defaultFont = workbook.createFont();
		defaultFont.setFontHeightInPoints((short) 10);
		defaultFont.setFontName("Arial");
	}

	public static void initDefaultFontBold() {
		defaultFontBold = workbook.createFont();
		defaultFontBold.setFontHeightInPoints((short) 10);
		defaultFontBold.setFontName("Arial");
		defaultFontBold.setBold(true);
	}

	private static void initTitleFont() {
		titleFont = workbook.createFont();
		titleFont.setBold(true);
		titleFont.setFontHeightInPoints((short) 14);
		titleFont.setFontName("Arial");
		
		titleFontBigger = workbook.createFont();
		titleFontBigger.setBold(true);
		titleFontBigger.setFontHeightInPoints((short) 16);
		titleFontBigger.setFontName("Arial");
	}

	public static void initTitleFontStyle() {

		titleFontStyle = workbook.createCellStyle();
		titleFontStyle.setFont(titleFont);

		titleFontStyleCenter = workbook.createCellStyle();
		titleFontStyleCenter.setFont(titleFont);
		titleFontStyleCenter.setBorderTop(BorderStyle.THIN);
		titleFontStyleCenter.setBorderBottom(BorderStyle.THIN);
		titleFontStyleCenter.setBorderLeft(BorderStyle.THIN);
		titleFontStyleCenter.setBorderRight(BorderStyle.THIN);
		titleFontStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		titleFontStyleCenter.setAlignment(HorizontalAlignment.CENTER);

		titleFontStyleCenterWithoutBorder = workbook.createCellStyle();
		titleFontStyleCenterWithoutBorder.setFont(titleFont);
		titleFontStyleCenterWithoutBorder.setVerticalAlignment(VerticalAlignment.CENTER);
		titleFontStyleCenterWithoutBorder.setAlignment(HorizontalAlignment.CENTER);
		titleFontStyleCenterWithoutBorder.setBorderTop(BorderStyle.NONE);
		titleFontStyleCenterWithoutBorder.setBorderBottom(BorderStyle.NONE);
		titleFontStyleCenterWithoutBorder.setBorderLeft(BorderStyle.NONE);
		titleFontStyleCenterWithoutBorder.setBorderRight(BorderStyle.NONE);

		titleFontStyleLeftAlign = workbook.createCellStyle();
		titleFontStyleLeftAlign.setFont(titleFont);
		titleFontStyleLeftAlign.setBorderTop(BorderStyle.THIN);
		titleFontStyleLeftAlign.setBorderBottom(BorderStyle.THIN);
		titleFontStyleLeftAlign.setBorderLeft(BorderStyle.THIN);
		titleFontStyleLeftAlign.setBorderRight(BorderStyle.THIN);
		titleFontStyleLeftAlign.setVerticalAlignment(VerticalAlignment.CENTER);
		titleFontStyleLeftAlign.setAlignment(HorizontalAlignment.LEFT);

		titleFontStyleBigger = workbook.createCellStyle();
		titleFontStyleBigger.setFont(titleFontBigger);

		titleFontStyleCenterBigger = workbook.createCellStyle();
		titleFontStyleCenterBigger.setFont(titleFontBigger);
		titleFontStyleCenterBigger.setBorderTop(BorderStyle.THIN);
		titleFontStyleCenterBigger.setBorderBottom(BorderStyle.THIN);
		titleFontStyleCenterBigger.setBorderLeft(BorderStyle.THIN);
		titleFontStyleCenterBigger.setBorderRight(BorderStyle.THIN);
		titleFontStyleCenterBigger.setVerticalAlignment(VerticalAlignment.CENTER);
		titleFontStyleCenterBigger.setAlignment(HorizontalAlignment.CENTER);

		titleFontStyleCenterWithoutBorderBigger = workbook.createCellStyle();
		titleFontStyleCenterWithoutBorderBigger.setFont(titleFontBigger);
		titleFontStyleCenterWithoutBorderBigger.setVerticalAlignment(VerticalAlignment.CENTER);
		titleFontStyleCenterWithoutBorderBigger.setAlignment(HorizontalAlignment.CENTER);
		titleFontStyleCenterWithoutBorderBigger.setBorderTop(BorderStyle.NONE);
		titleFontStyleCenterWithoutBorderBigger.setBorderBottom(BorderStyle.NONE);
		titleFontStyleCenterWithoutBorderBigger.setBorderLeft(BorderStyle.NONE);
		titleFontStyleCenterWithoutBorderBigger.setBorderRight(BorderStyle.NONE);

		titleFontStyleLeftAlignBigger = workbook.createCellStyle();
		titleFontStyleLeftAlignBigger.setFont(titleFontBigger);
		titleFontStyleLeftAlignBigger.setBorderTop(BorderStyle.THIN);
		titleFontStyleLeftAlignBigger.setBorderBottom(BorderStyle.THIN);
		titleFontStyleLeftAlignBigger.setBorderLeft(BorderStyle.THIN);
		titleFontStyleLeftAlignBigger.setBorderRight(BorderStyle.THIN);
		titleFontStyleLeftAlignBigger.setVerticalAlignment(VerticalAlignment.CENTER);
		titleFontStyleLeftAlignBigger.setAlignment(HorizontalAlignment.LEFT);
		
	}

	public static void initDefaultFontStyle() {
		defaultFontStyle = workbook.createCellStyle();
		defaultFontStyle.setFont(defaultFont);
		defaultFontStyle.setWrapText(true);
		defaultFontStyle.setBorderBottom(BorderStyle.THIN);
		defaultFontStyle.setBorderTop(BorderStyle.THIN);
		defaultFontStyle.setBorderRight(BorderStyle.THIN);
		defaultFontStyle.setBorderLeft(BorderStyle.THIN);
		defaultFontStyle.setVerticalAlignment(VerticalAlignment.TOP);
		
		defaultFontStyleWithoutBorder = workbook.createCellStyle();
		defaultFontStyleWithoutBorder.setFont(defaultFont);
		defaultFontStyleWithoutBorder.setBorderBottom(BorderStyle.NONE);
		defaultFontStyleWithoutBorder.setBorderTop(BorderStyle.NONE);
		defaultFontStyleWithoutBorder.setBorderRight(BorderStyle.NONE);
		defaultFontStyleWithoutBorder.setBorderLeft(BorderStyle.NONE);

		defaultFontStyleDateFormat = workbook.createCellStyle();
		defaultFontStyleDateFormat.setFont(defaultFont);
		defaultFontStyleDateFormat.setWrapText(true);
		defaultFontStyleDateFormat.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleDateFormat.setBorderTop(BorderStyle.THIN);
		defaultFontStyleDateFormat.setBorderRight(BorderStyle.THIN);
		defaultFontStyleDateFormat.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleDateFormat.setVerticalAlignment(VerticalAlignment.TOP);
		CreationHelper createHelper = workbook.getCreationHelper();
		defaultFontStyleDateFormat.setDataFormat(createHelper.createDataFormat().getFormat("yyyymmdd"));

		defaultFontStyleSessionFirstLine = workbook.createCellStyle();
		defaultFontStyleSessionFirstLine.setFont(defaultFont);
		defaultFontStyleSessionFirstLine.setWrapText(true);
		defaultFontStyleSessionFirstLine.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleSessionFirstLine.setBorderTop(BorderStyle.THICK);
		defaultFontStyleSessionFirstLine.setBorderRight(BorderStyle.THIN);
		defaultFontStyleSessionFirstLine.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleSessionFirstLine.setVerticalAlignment(VerticalAlignment.TOP);

		defaultFontStyleSessionFirstLineLeftGrid = workbook.createCellStyle();
		defaultFontStyleSessionFirstLineLeftGrid.setFont(defaultFont);
		defaultFontStyleSessionFirstLineLeftGrid.setWrapText(true);
		defaultFontStyleSessionFirstLineLeftGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleSessionFirstLineLeftGrid.setBorderTop(BorderStyle.THICK);
		defaultFontStyleSessionFirstLineLeftGrid.setBorderRight(BorderStyle.NONE);
		defaultFontStyleSessionFirstLineLeftGrid.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleSessionFirstLineLeftGrid.setVerticalAlignment(VerticalAlignment.TOP);

		defaultFontStyleSessionFirstLineRightGrid = workbook.createCellStyle();
		defaultFontStyleSessionFirstLineRightGrid.setFont(defaultFont);
		defaultFontStyleSessionFirstLineRightGrid.setWrapText(true);
		defaultFontStyleSessionFirstLineRightGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleSessionFirstLineRightGrid.setBorderTop(BorderStyle.THICK);
		defaultFontStyleSessionFirstLineRightGrid.setBorderRight(BorderStyle.THIN);
		defaultFontStyleSessionFirstLineRightGrid.setBorderLeft(BorderStyle.NONE);
		defaultFontStyleSessionFirstLineRightGrid.setVerticalAlignment(VerticalAlignment.TOP);

		defaultFontStyleBold = workbook.createCellStyle();
		defaultFontStyleBold.setFont(defaultFontBold);
		defaultFontStyleBold.setWrapText(true);
		defaultFontStyleBold.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleBold.setBorderTop(BorderStyle.THIN);
		defaultFontStyleBold.setBorderRight(BorderStyle.THIN);
		defaultFontStyleBold.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleBold.setVerticalAlignment(VerticalAlignment.TOP);

		defaultFontStyleBoldWithoutBorder = workbook.createCellStyle();
		defaultFontStyleBoldWithoutBorder.setFont(defaultFontBold);
		defaultFontStyleBoldWithoutBorder.setWrapText(true);
		defaultFontStyleBoldWithoutBorder.setVerticalAlignment(VerticalAlignment.TOP);

		defaultFontStyleCenter = workbook.createCellStyle();
		defaultFontStyleCenter.setFont(defaultFont);
		defaultFontStyleCenter.setWrapText(true);
		defaultFontStyleCenter.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleCenter.setBorderTop(BorderStyle.THIN);
		defaultFontStyleCenter.setBorderRight(BorderStyle.THIN);
		defaultFontStyleCenter.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleCenter.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleCenter.setAlignment(HorizontalAlignment.CENTER);

		defaultFontStyleCenterLeftGrid = workbook.createCellStyle();
		defaultFontStyleCenterLeftGrid.setFont(defaultFont);
		defaultFontStyleCenterLeftGrid.setWrapText(true);
		defaultFontStyleCenterLeftGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleCenterLeftGrid.setBorderTop(BorderStyle.THIN);
		defaultFontStyleCenterLeftGrid.setBorderRight(BorderStyle.NONE);
		defaultFontStyleCenterLeftGrid.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleCenterLeftGrid.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleCenterLeftGrid.setAlignment(HorizontalAlignment.CENTER);

		defaultFontStyleCenterRightGrid = workbook.createCellStyle();
		defaultFontStyleCenterRightGrid.setFont(defaultFont);
		defaultFontStyleCenterRightGrid.setWrapText(true);
		defaultFontStyleCenterRightGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleCenterRightGrid.setBorderTop(BorderStyle.THIN);
		defaultFontStyleCenterRightGrid.setBorderRight(BorderStyle.THIN);
		defaultFontStyleCenterRightGrid.setBorderLeft(BorderStyle.NONE);
		defaultFontStyleCenterRightGrid.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleCenterRightGrid.setAlignment(HorizontalAlignment.CENTER);

		defaultFontStyleCenterBold = workbook.createCellStyle();
		defaultFontStyleCenterBold.setFont(defaultFontBold);
		defaultFontStyleCenterBold.setWrapText(true);
		defaultFontStyleCenterBold.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleCenterBold.setBorderTop(BorderStyle.THIN);
		defaultFontStyleCenterBold.setBorderRight(BorderStyle.THIN);
		defaultFontStyleCenterBold.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleCenterBold.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleCenterBold.setAlignment(HorizontalAlignment.CENTER);

		defaultFontStyleCenterBoldBottomAlign = workbook.createCellStyle();
		defaultFontStyleCenterBoldBottomAlign.setFont(defaultFontBold);
		defaultFontStyleCenterBoldBottomAlign.setWrapText(true);
		defaultFontStyleCenterBoldBottomAlign.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleCenterBoldBottomAlign.setBorderTop(BorderStyle.THIN);
		defaultFontStyleCenterBoldBottomAlign.setBorderRight(BorderStyle.THIN);
		defaultFontStyleCenterBoldBottomAlign.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleCenterBoldBottomAlign.setVerticalAlignment(VerticalAlignment.BOTTOM);
		defaultFontStyleCenterBoldBottomAlign.setAlignment(HorizontalAlignment.CENTER);

		defaultFontStyleRightAlign = workbook.createCellStyle();
		defaultFontStyleRightAlign.setFont(defaultFont);
		defaultFontStyleRightAlign.setWrapText(true);
		defaultFontStyleRightAlign.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleRightAlign.setBorderTop(BorderStyle.THIN);
		defaultFontStyleRightAlign.setBorderRight(BorderStyle.THIN);
		defaultFontStyleRightAlign.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleRightAlign.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleRightAlign.setAlignment(HorizontalAlignment.RIGHT);

		defaultFontStyleLeftAlign = workbook.createCellStyle();
		defaultFontStyleLeftAlign.setFont(defaultFont);
		defaultFontStyleLeftAlign.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleLeftAlign.setBorderTop(BorderStyle.THIN);
		defaultFontStyleLeftAlign.setBorderRight(BorderStyle.THIN);
		defaultFontStyleLeftAlign.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleLeftAlign.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleLeftAlign.setAlignment(HorizontalAlignment.LEFT);

		defaultFontStyleLeftAlignLIBSessionStyle = workbook.createCellStyle();
		defaultFontStyleLeftAlignLIBSessionStyle.setFont(defaultFontBold);
		defaultFontStyleLeftAlignLIBSessionStyle.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleLeftAlignLIBSessionStyle.setBorderTop(BorderStyle.THICK);
		defaultFontStyleLeftAlignLIBSessionStyle.setBorderRight(BorderStyle.THIN);
		defaultFontStyleLeftAlignLIBSessionStyle.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleLeftAlignLIBSessionStyle.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleLeftAlignLIBSessionStyle.setAlignment(HorizontalAlignment.LEFT);

		defaultFontStyleLeftAlignLIBSessionStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
		defaultFontStyleLeftAlignLIBSessionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		defaultFontStyleRightAlignBold = workbook.createCellStyle();
		defaultFontStyleRightAlignBold.setFont(defaultFontBold);
		defaultFontStyleRightAlignBold.setWrapText(true);
		defaultFontStyleRightAlignBold.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleRightAlignBold.setBorderTop(BorderStyle.THIN);
		defaultFontStyleRightAlignBold.setBorderRight(BorderStyle.THIN);
		defaultFontStyleRightAlignBold.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleRightAlignBold.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleRightAlignBold.setAlignment(HorizontalAlignment.RIGHT);

		defaultFontStyleWithoutBorderRightAlign = workbook.createCellStyle();
		defaultFontStyleWithoutBorderRightAlign.setFont(defaultFont);
		defaultFontStyleWithoutBorderRightAlign.setAlignment(HorizontalAlignment.RIGHT);
		defaultFontStyleWithoutBorderRightAlign.setVerticalAlignment(VerticalAlignment.TOP);

		defaultFontStyleGreyFill = workbook.createCellStyle();
		defaultFontStyleGreyFill.setFont(defaultFont);
		defaultFontStyleGreyFill.setWrapText(true);
		defaultFontStyleGreyFill.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleGreyFill.setBorderTop(BorderStyle.THIN);
		defaultFontStyleGreyFill.setBorderRight(BorderStyle.THIN);
		defaultFontStyleGreyFill.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleGreyFill.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleGreyFill.setAlignment(HorizontalAlignment.CENTER);
		defaultFontStyleGreyFill.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		defaultFontStyleGreyFill.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		defaultFontStyleGreyFillLeftGrid = workbook.createCellStyle();
		defaultFontStyleGreyFillLeftGrid.setFont(defaultFont);
		defaultFontStyleGreyFillLeftGrid.setWrapText(true);
		defaultFontStyleGreyFillLeftGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleGreyFillLeftGrid.setBorderTop(BorderStyle.THIN);
		defaultFontStyleGreyFillLeftGrid.setBorderRight(BorderStyle.NONE);
		defaultFontStyleGreyFillLeftGrid.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleGreyFillLeftGrid.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleGreyFillLeftGrid.setAlignment(HorizontalAlignment.CENTER);
		defaultFontStyleGreyFillLeftGrid.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		defaultFontStyleGreyFillLeftGrid.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		defaultFontStyleGreyFillRightGrid = workbook.createCellStyle();
		defaultFontStyleGreyFillRightGrid.setFont(defaultFont);
		defaultFontStyleGreyFillRightGrid.setWrapText(true);
		defaultFontStyleGreyFillRightGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleGreyFillRightGrid.setBorderTop(BorderStyle.THIN);
		defaultFontStyleGreyFillRightGrid.setBorderRight(BorderStyle.THIN);
		defaultFontStyleGreyFillRightGrid.setBorderLeft(BorderStyle.NONE);
		defaultFontStyleGreyFillRightGrid.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleGreyFillRightGrid.setAlignment(HorizontalAlignment.CENTER);
		defaultFontStyleGreyFillRightGrid.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		defaultFontStyleGreyFillRightGrid.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		defaultFontStyleGreyFillSessionFirstLine = workbook.createCellStyle();
		defaultFontStyleGreyFillSessionFirstLine.setFont(defaultFont);
		defaultFontStyleGreyFillSessionFirstLine.setWrapText(true);
		defaultFontStyleGreyFillSessionFirstLine.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLine.setBorderTop(BorderStyle.THICK);
		defaultFontStyleGreyFillSessionFirstLine.setBorderRight(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLine.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLine.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleGreyFillSessionFirstLine.setAlignment(HorizontalAlignment.CENTER);
		defaultFontStyleGreyFillSessionFirstLine.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		defaultFontStyleGreyFillSessionFirstLine.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		defaultFontStyleGreyFillSessionFirstLineLeftGrid = workbook.createCellStyle();
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setFont(defaultFont);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setWrapText(true);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setBorderTop(BorderStyle.THICK);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setBorderRight(BorderStyle.NONE);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setBorderLeft(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setAlignment(HorizontalAlignment.CENTER);
		defaultFontStyleGreyFillSessionFirstLineLeftGrid
				.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		defaultFontStyleGreyFillSessionFirstLineLeftGrid.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		defaultFontStyleGreyFillSessionFirstLineRightGrid = workbook.createCellStyle();
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setFont(defaultFont);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setWrapText(true);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setBorderBottom(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setBorderTop(BorderStyle.THICK);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setBorderRight(BorderStyle.THIN);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setBorderLeft(BorderStyle.NONE);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setVerticalAlignment(VerticalAlignment.TOP);
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setAlignment(HorizontalAlignment.CENTER);
		defaultFontStyleGreyFillSessionFirstLineRightGrid
				.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		defaultFontStyleGreyFillSessionFirstLineRightGrid.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	}

}
