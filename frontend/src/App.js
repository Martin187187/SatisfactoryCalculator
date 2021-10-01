import { items } from "./Data";

/*
const App = () => {
    const [notes, setNotes] = useState([])

    useEffect(() => {
        async function fetchData() {
            const f = await axios.get('http://localhost:3001/notes')
            console.log(f.data)
            setNotes(f.data)
        }
        fetchData()
    }, [])


    
 return <div></div>   
}
*/
const App = () => {
    console.log(items)
    
 return <div></div>   
}
export default App;