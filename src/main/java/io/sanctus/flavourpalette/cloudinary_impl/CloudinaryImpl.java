package io.sanctus.flavourpalette.cloudinary_impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
@SuppressWarnings("unused")
public class CloudinaryImpl {

  private static final Cloudinary cloudinary = new Cloudinary();
  @SuppressWarnings("rawtypes")
  private static final Map params = ObjectUtils.asMap("folder", "recipes", "resource_type", "image");

  public CloudinaryImpl() {
    CloudinaryImpl.cloudinary.config.secure = true;
  }

  @SuppressWarnings("rawtypes")
  public static Map uploadFile(MultipartFile multipartFile) throws IOException {
    return cloudinary.uploader().upload(multipartFile.getBytes(), params);
  }

  // Returns a string of the full images URL
  public static String getFullImageURL(String recipeDTO) {
    // The API line also works to retrieve the image from the API - but we are linking directly from source
    return "https://res.cloudinary.com/{PLACEHOLDER}/image/upload/f_auto,q_auto/v1/" + recipeDTO;
  }

  // Returns a string for the thumbnail sized image URL
  public static String getThumbImageURL(String recipeDTO) {
    return "https://res.cloudinary.com/{PLACEHOLDER}/image/upload/c_scale,w_310/" + recipeDTO;
  }

  // Returns a string for the image that is sized at 120px - used as a tiny preview on the edit page
  public static String getSmallImageURL(String recipeDTO) {
    return "https://res.cloudinary.com/{PLACEHOLDER}/image/upload/c_scale,w_120/" + recipeDTO;
  }

  public static void deleteImage(String publicId) throws IOException {
    cloudinary.uploader().destroy(publicId, params);
  }
}
