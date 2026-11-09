package com.websitescreenshotworld;

import java.util.Map;

/**
 * The file format of a capture. It must belong to the capture's {@link MediaCategory}.
 *
 * <p>Unknown values come through {@link #of(String)} unchanged.
 */
public final class CaptureFormat extends Token {
  /** PNG image. */
  public static final CaptureFormat PNG = new CaptureFormat("png");

  /** JPEG image. */
  public static final CaptureFormat JPEG = new CaptureFormat("jpeg");

  /** WebP image. */
  public static final CaptureFormat WEBP = new CaptureFormat("webp");

  /** TIFF image. */
  public static final CaptureFormat TIFF = new CaptureFormat("tiff");

  /** MP4 video. */
  public static final CaptureFormat MP4 = new CaptureFormat("mp4");

  /** WebM video. */
  public static final CaptureFormat WEBM = new CaptureFormat("webm");

  /** QuickTime video. */
  public static final CaptureFormat MOV = new CaptureFormat("mov");

  /** Animated GIF. */
  public static final CaptureFormat GIF = new CaptureFormat("gif");

  /** PDF holding an image of the page. */
  public static final CaptureFormat IMAGE_PDF = new CaptureFormat("image_pdf");

  /** PDF printed from the page, with selectable text. */
  public static final CaptureFormat DOCUMENT_PDF = new CaptureFormat("document_pdf");

  private static final Map<String, CaptureFormat> KNOWN =
      index(PNG, JPEG, WEBP, TIFF, MP4, WEBM, MOV, GIF, IMAGE_PDF, DOCUMENT_PDF);

  private CaptureFormat(String value) {
    super(value);
  }

  /**
   * Returns the CaptureFormat for a wire value.
   *
   * @param value the value as the API spells it
   * @return the known constant, or a new instance for a value this library does not know
   */
  public static CaptureFormat of(String value) {
    return lookup(KNOWN, value, CaptureFormat::new);
  }
}
