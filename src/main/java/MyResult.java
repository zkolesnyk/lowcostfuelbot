import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;

final class MyResult {
    private final InlineKeyboardMarkup first;
    private final String previous;
    private final String next;

    public MyResult(InlineKeyboardMarkup first, String second, String third) {
        this.first = first;
        this.previous = second;
        this.next = third;
    }

    public InlineKeyboardMarkup getInlineKeyboardMarkup() {
        return first;
    }

    public String getPrevious() {
        return previous;
    }

    public String getNext() {
        return next;
    }
}