import React, { useRef, useState } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import { OrbitControls, useGLTF } from "@react-three/drei";
import theme from "../../common/styles/theme";
import { useMediaQuery } from "@mui/material";

type GLBViewerProps = {
  glbUrl: string;
};

type ModelProps = {
  glbUrl: string;
  isInteracting: boolean;
  setIsInteracting: React.Dispatch<React.SetStateAction<boolean>>;
};

const Model = ({ glbUrl, isInteracting, setIsInteracting }: ModelProps) => {
  const { scene } = useGLTF(glbUrl) as any;
  const ref = useRef();
  const { gl } = useThree();

  const onPointerOver = () => {
    gl.domElement.style.cursor = "pointer";
  };

  const onPointerOut = () => {
    gl.domElement.style.cursor = "auto";
  };

  useFrame(() => {
    if (!isInteracting) {
      ref.current.rotation.y += 0.01;
    }
  });

  return (
    <group
      ref={ref}
      scale={[12, 12, 12]}
      position={[0, -1.6, 0]}
      rotation={[0, Math.PI / 2, 0]} // Rotate the model 180 degrees around the Y-axis
    >
      <primitive
        object={scene}
        onPointerOver={onPointerOver}
        onPointerOut={onPointerOut}
      />
    </group>
  );
};

const GLBViewer: React.FC<GLBViewerProps> = ({ glbUrl }) => {
  const [isInteracting, setIsInteracting] = useState(false);
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  return (
    <div style={{ height: "600px", width: "100%" }}>
      <Canvas
        gl={{ alpha: true }}
        camera={{
          position: [3.5, 0.87, -1.95],
          fov: 50,
          near: 0.1,
          far: 1000,
        }}
      >
        <ambientLight intensity={0.5} />
        <spotLight position={[10, 10, 10]} angle={0.15} penumbra={1} />
        <pointLight position={[-10, -10, -10]} />
        <Model
          glbUrl={glbUrl}
          isInteracting={isInteracting}
          setIsInteracting={setIsInteracting}
        />
        <OrbitControls
          enableZoom={false}
          onStart={() => setIsInteracting(true)}
          onEnd={() => setIsInteracting(false)}
        />
      </Canvas>
    </div>
  );
};

export default GLBViewer;
