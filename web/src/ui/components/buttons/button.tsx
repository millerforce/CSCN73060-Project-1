import styles from "@styles/components/buttons/button.module.css";

interface ButtonProps {
  count?: number;
  disabled?: boolean;
  onClick?: () => void;
  iconString?: string;
}

export default function Button({
  count,
  disabled = false,
  onClick,
  iconString,
}: Readonly<ButtonProps>) {
  return (
    <button className={styles.button} disabled={disabled} onClick={onClick}>
      <span className="material-symbols-outlined">{iconString}</span>
      {count}
    </button>
  );
}
