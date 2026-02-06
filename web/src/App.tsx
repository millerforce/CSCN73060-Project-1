import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import { PostCard } from "./components/post.tsx";

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      {/*<div>*/}
      {/*  <a href="https://vite.dev" target="_blank">*/}
      {/*    <img src={viteLogo} className="logo" alt="Vite logo" />*/}
      {/*  </a>*/}
      {/*  <a href="https://react.dev" target="_blank">*/}
      {/*    <img src={reactLogo} className="logo react" alt="React logo" />*/}
      {/*  </a>*/}
      {/*</div>*/}
      {/*<h1>Vite + React</h1>*/}
      {/*<div className="card">*/}
      {/*  <button onClick={() => setCount((count) => count + 1)}>*/}
      {/*    count is {count}*/}
      {/*  </button>*/}
      {/*  <p>*/}
      {/*    Edit <code>src/App.tsx</code> and save to test HMR*/}
      {/*  </p>*/}
      {/*</div>*/}
      {/*<p className="read-the-docs">*/}
      {/*  Click on the Vite and React logos to learn more*/}
      {/*</p>*/}
        <PostCard id={"test"} author={{id: "test", username: "username", createdAt: "2025-5-0"}} content={"My post"} numberOfLikes={5} numberOfComments={10} createdAt={"2025-5-3"} updatedAt={"2025-5-5"}></PostCard>
    </>
  )
}

export default App
