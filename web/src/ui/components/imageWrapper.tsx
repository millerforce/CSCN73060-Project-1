import styles from "@styles/components/imageWrapper.module.css";

interface ImageWrapperProps {
  src: string;
  alt?: string;
  width?: string | number;
  height?: string | number;
  className?: string;
}

export default function ImageWrapper({
  src,
  alt,
  width = "100%",
  height = "auto",
  className = "",
}: Readonly<ImageWrapperProps>) {
  return (
    <div
      className={`${styles.container} ${className}`}
      style={{ width, height }}
    >
      <img src={src} alt={alt} className={styles.image} />
    </div>
  );
}
