import java.util.List;

public class Lol {

    public static void main(String[] args) {

        ListItem<Double> a = new ListItem<>(3.0);
        ListItem<Double> b = new ListItem<>(1.0);
        ListItem<Double> c = new ListItem<>(5.0);
        a.next = b;
        b.next = c;
        ListItem<ListItem<Double>> first = new ListItem<>(a);

        ListItem<Double> d = new ListItem<>(2.0);
        ListItem<Double> e = new ListItem<>(1.0);
        d.next = e;
        ListItem<ListItem<Double>> second = new ListItem<>(d);
        first.next = second;


        System.out.println(getLOL(first, 5));
    }

    public static class ListItem<T>{
        public T key;
        public ListItem<T> next;

        public ListItem(T key){
            this.key = key;
        }

        @Override
        public String toString() {
            return "ListItem{" +
                    "key=" + key +
                    ", next=" + next +
                    '}';
        }
    }
    public static ListItem<ListItem<Double>> getLOL(ListItem<ListItem<Double>> input, double max){

        ListItem<ListItem<Double>> currentOuter = input;
        while(currentOuter!=null){

            ListItem<Double> currentInner = currentOuter.key;
            ListItem<Double> prev = null;
            double sum = 0;
            while(currentInner!=null){

                double value = currentInner.key;
                sum+=value;

                //insert new outer ListItem
                if(sum>max){
                    if(prev==null)
                        throw new RuntimeException();

                    ListItem<ListItem<Double>> newListItem = new ListItem<>(currentInner);

                    newListItem.next = currentOuter.next;
                    currentOuter.next = newListItem;
                    prev.next = null;

                }
                prev = currentInner;
                currentInner = currentInner.next;
            }

            currentOuter = currentOuter.next;
        }
        return input;
    }

}
