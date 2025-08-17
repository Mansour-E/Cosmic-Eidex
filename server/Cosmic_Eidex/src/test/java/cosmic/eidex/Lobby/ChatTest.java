package cosmic.eidex.Lobby;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    Chat chat;
    Chat chat2;
    List<Nachricht> list;

    @BeforeEach
    void setUp() {
        List<Nachricht> list = new ArrayList<>();
        list.add(new Nachricht("sender1", "Nachricht1"));
        list.add(new Nachricht("sender2", "Nachricht2"));
        list.add(new Nachricht("sender3", "Nachricht3"));
        chat = new Chat(list);

    }

    @Test
    void testNullChat() {
        chat2 = new Chat();
        assertNull(chat2.getNachrichten());
    }
    @Test
    void testGetNachrichten() {
        Chat chat = new Chat(list);
        assertEquals(list, chat.getNachrichten());

    }
}