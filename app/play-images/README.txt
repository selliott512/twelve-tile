Some of these images were downloaded from the play store.  Starting at:
  https://play.google.com/store/apps/details?id=org.selliott.twelvetile
The image was selected and downloaded.  Here's the URL for the hi-res icon:
  https://lh3.ggpht.com/V1K6VkfVJYFCsoR6LrU2B574W6QYEX4DbqSa6ozJNZ_DaoTC_hK9eJlOkR2UUrqCH_s=w300-rw
Note the "w300-rw" at the end of the URL.  The "300" seems to refer to the
size of the image.  Changing it to "512" expands it:
  https://lh3.ggpht.com/V1K6VkfVJYFCsoR6LrU2B574W6QYEX4DbqSa6ozJNZ_DaoTC_hK9eJlOkR2UUrqCH_s=w512-rw
The maximum size seems to be 512x512, or close to it.

The image is converted to PNG using "convert" from version 6.8.6.3 of ImageMagick:
  convert hi-res-icon-300x300.webp hi-res-icon-300x300.png
  convert hi-res-icon-512x512.webp hi-res-icon-512x512.png
