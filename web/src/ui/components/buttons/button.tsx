import styles from "@styles/components/buttons/button.module.css";

interface ButtonProps {
  count?: number;
  disabled?: boolean;
  onClick?: () => void;
  iconString?: string;
  text?: string;
  clickable?: boolean;
}

export default function Button({
  count,
  disabled = false,
  onClick,
  iconString,
  text,
  clickable = true,
}: Readonly<ButtonProps>) {
  return (
    <button
      className={`${styles.button} ${clickable ? "" : styles.nonClickable}`}
      disabled={disabled}
      onClick={onClick}
    >
      <span className="material-symbols-outlined">{iconString}</span>
      <div>{count}</div>
      {text}
    </button>
  );
}
