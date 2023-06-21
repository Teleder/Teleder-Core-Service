package teleder.core.utils;

import lombok.Data;

@Data
public class CONSTS {
    // MESSAGE
    public final static String MESSAGE_PRIVATE = "USER";
    public final static String MESSAGE_GROUP = "GROUP";
    public final static String REQUEST_FIEND = "REQUEST_FIEND";
    public final static String READ_RECEIPT = "read";
    public final static String DELIVERY_RECEIPT = "delivery";
    public final static String CHATTING = "CHATTING";
    public final static String NEW_GROUP = "NEW_GROUP";
    public final static String STOP_CHATTING = "STOP_CHATTING";
    public final static String EMOJI = "EMOJI";
    public final static String EDIT_MESSAGE = "EDIT_MESSAGE";
    public final static String DELETE_MESSAGE = "DELETE_MESSAGE";

    public final static String CALL = "CALL";
    public final static String IMAGE = "IMAGE";
    public final static String AUDIO = "AUDIO";
    public final static String  VIDEO = "VIDEO";
    public final static String FILE = "FILE";
    public final static String CUSTOM = "CUSTOM";
    public final static String CALL_TYPE_VIDEO = "CALL_TYPE.VIDEO";
    public final static String CALL_TYPE_AUDIO = "CALL_TYPE.AUDIO";
    public final static String CATEGORY_MESSAGE = "CATEGORY_MESSAGE";
    public final static String CATEGORY_ACTION = "CATEGORY_ACTION";
    public final static String CATEGORY_CALL = "CATEGORY_CALL";
    public final static String DIRECT_CALL = "DIRECT_CALL";
    public final static String STICKER = "extension_sticker";
    public final static String NEW_REACTION = "NEW_REACTION";
    // CONTACT
    public final static String BLOCK_CONTACT = "BLOCK_CONTACT";
    public final static String ADD_CONTACT = "ADD_CONTACT";
    public final static String REMOVE_CONTACT = "REMOVE_CONTACT";
    public final static String REMOVE_BLOCK_CONTACT = "REMOVE_BLOCK_CONTACT";
    public final static String ACCEPT_CONTACT = "ACCEPT_CONTACT";
    public final static String DENY_CONTACT = "DENY_CONTACT";
    //GROUP
    public final static String LEAVE_GROUP = "LEAVE_GROUP";
    public final static String BLOCK_MEMBER_GROUP = "BLOCK_MEMBER_GROUP";
    public final static String ADD_MEMBER_TO_GROUP = "ADD_MEMBER_TO_GROUP";
    public final static String REQUEST_MEMBER_TO_GROUP = "REQUEST_MEMBER_TO_GROUP";
    public final static String REMOVE_MEMBER = "REMOVE_MEMBER";
    public final static String REMOVE_BLOCK_MEMBER_GROUP = "REMOVE_BLOCK_MEMBER_GROUP";
    public final static String DECENTRALIZATION = "DECENTRALIZATION";
    public final static String ACCEPT_MEMBER_JOIN = "ACCEPT_MEMBER_JOIN";
    public final static String DENY_MEMBER_JOIN = "DENY_MEMBER_JOIN";
    public final static String CREATE_ROLE = "CREATE_ROLE";
    public final static String DELETE_ROLE = "DELETE_ROLE";
}
