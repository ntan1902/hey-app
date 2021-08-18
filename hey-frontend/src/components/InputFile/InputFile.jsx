import React from "react";

const InputFile = ({ name, children, onChange }) => {
  return (
    <div>
      <label htmlFor="input-file">{children}</label>
      <input
        type="file"
        name={name}
        onChange={onChange}
        id="input-file"
        style={{ visibility: "hidden" }}
      />
    </div>
  );
};

export default InputFile;
