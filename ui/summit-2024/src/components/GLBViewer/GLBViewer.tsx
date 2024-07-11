import React, { useRef, useState, useCallback } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import { OrbitControls, useGLTF } from "@react-three/drei";

type GLBViewerProps = {
  glbUrl: string;
  height: string;
  width: string;
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

  const handleDoubleClick = useCallback(() => {
    setIsInteracting(!isInteracting);
  }, [isInteracting, setIsInteracting]);

  useFrame(() => {
    if (!isInteracting) {
      // @ts-ignore
      ref.current.rotation.y += 0.01;
    }
  });

  return (
    <group
      // @ts-ignore
      ref={ref}
      scale={[12, 12, 12]}
      position={[0, -1.6, 0]}
      rotation={[0, Math.PI / 2, 0]}
      onDoubleClick={handleDoubleClick}
      onPointerOver={onPointerOver}
      onPointerOut={onPointerOut}
    >
      <primitive object={scene} />
    </group>
  );
};

const GLBViewer: React.FC<GLBViewerProps> = ({ glbUrl, height, width }) => {
  const [isInteracting, setIsInteracting] = useState(false);

  return (
    <div style={{ height: height, width: width }}>
      <Canvas
        gl={{ alpha: true }}
        camera={{
          position: [3.5, 0.87, -1.95],
          fov: 50,
          near: 0.1,
          far: 1000,
        }}
      >
        <ambientLight intensity={1} />
        <spotLight position={[10, 10, 10]} angle={0.15} penumbra={1} />
        <pointLight position={[0, 0, 10]} intensity={1.5} />
        <Model
          glbUrl={glbUrl}
          isInteracting={isInteracting}
          setIsInteracting={setIsInteracting}
        />
        <OrbitControls maxDistance={4} minDistance={4} enabled={true} />
      </Canvas>
    </div>
  );
};

export default GLBViewer;
