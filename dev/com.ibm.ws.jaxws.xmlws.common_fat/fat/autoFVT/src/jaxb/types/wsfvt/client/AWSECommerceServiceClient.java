//
// @(#) 1.1 autoFVT/src/jaxb/types/wsfvt/client/AWSECommerceServiceClient.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 1/29/08 08:46:12 [8/8/12 06:57:15]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 11/14/07 ulbricht    290938          New File
//

package jaxb.types.wsfvt.client;

import java.util.List;
import java.util.ArrayList;

import java.math.BigInteger;

import javax.xml.ws.Holder;

import javax.jws.WebService;

import jaxb.types.wsfvt.server.amazon.Accessories;
import jaxb.types.wsfvt.server.amazon.Address;
import jaxb.types.wsfvt.server.amazon.Arguments;
import jaxb.types.wsfvt.server.amazon.AWSECommerceService;
import jaxb.types.wsfvt.server.amazon.BrowseNode;
import jaxb.types.wsfvt.server.amazon.BrowseNodeLookup;
import jaxb.types.wsfvt.server.amazon.BrowseNodeLookupRequest;
import jaxb.types.wsfvt.server.amazon.BrowseNodeLookupResponse;
import jaxb.types.wsfvt.server.amazon.BrowseNodes;
import jaxb.types.wsfvt.server.amazon.Cart;
import jaxb.types.wsfvt.server.amazon.CartAdd;
import jaxb.types.wsfvt.server.amazon.CartAddRequest;
import jaxb.types.wsfvt.server.amazon.CartAddResponse;
import jaxb.types.wsfvt.server.amazon.CartClear;
import jaxb.types.wsfvt.server.amazon.CartClearRequest;
import jaxb.types.wsfvt.server.amazon.CartClearResponse;
import jaxb.types.wsfvt.server.amazon.CartCreate;
import jaxb.types.wsfvt.server.amazon.CartCreateRequest;
import jaxb.types.wsfvt.server.amazon.CartCreateResponse;
import jaxb.types.wsfvt.server.amazon.CartGet;
import jaxb.types.wsfvt.server.amazon.CartGetRequest;
import jaxb.types.wsfvt.server.amazon.CartGetResponse;
import jaxb.types.wsfvt.server.amazon.CartItem;
import jaxb.types.wsfvt.server.amazon.CartItems;
import jaxb.types.wsfvt.server.amazon.CartModify;
import jaxb.types.wsfvt.server.amazon.CartModifyRequest;
import jaxb.types.wsfvt.server.amazon.CartModifyResponse;
import jaxb.types.wsfvt.server.amazon.Customer;
import jaxb.types.wsfvt.server.amazon.CustomerContentLookup;
import jaxb.types.wsfvt.server.amazon.CustomerContentLookupRequest;
import jaxb.types.wsfvt.server.amazon.CustomerContentLookupResponse;
import jaxb.types.wsfvt.server.amazon.CustomerContentSearch;
import jaxb.types.wsfvt.server.amazon.CustomerContentSearchRequest;
import jaxb.types.wsfvt.server.amazon.CustomerContentSearchResponse;
import jaxb.types.wsfvt.server.amazon.CustomerReviews;
import jaxb.types.wsfvt.server.amazon.Customers;
import jaxb.types.wsfvt.server.amazon.DecimalWithUnits;
import jaxb.types.wsfvt.server.amazon.EditorialReview;
import jaxb.types.wsfvt.server.amazon.EditorialReviews;
import jaxb.types.wsfvt.server.amazon.Errors;
import jaxb.types.wsfvt.server.amazon.Help;
import jaxb.types.wsfvt.server.amazon.HelpRequest;
import jaxb.types.wsfvt.server.amazon.HelpResponse;
import jaxb.types.wsfvt.server.amazon.HTTPHeaders;
import jaxb.types.wsfvt.server.amazon.Image;
import jaxb.types.wsfvt.server.amazon.Information;
import jaxb.types.wsfvt.server.amazon.Item;
import jaxb.types.wsfvt.server.amazon.ItemAttributes;
import jaxb.types.wsfvt.server.amazon.ItemLookup;
import jaxb.types.wsfvt.server.amazon.ItemLookupRequest;
import jaxb.types.wsfvt.server.amazon.ItemLookupResponse;
import jaxb.types.wsfvt.server.amazon.Items;
import jaxb.types.wsfvt.server.amazon.ItemSearch;
import jaxb.types.wsfvt.server.amazon.ItemSearchRequest;
import jaxb.types.wsfvt.server.amazon.ItemSearchResponse;
import jaxb.types.wsfvt.server.amazon.ListItem;
import jaxb.types.wsfvt.server.amazon.ListLookup;
import jaxb.types.wsfvt.server.amazon.ListLookupRequest;
import jaxb.types.wsfvt.server.amazon.ListLookupResponse;
import jaxb.types.wsfvt.server.amazon.ListmaniaLists;
import jaxb.types.wsfvt.server.amazon.Lists;
import jaxb.types.wsfvt.server.amazon.ListSearch;
import jaxb.types.wsfvt.server.amazon.ListSearchRequest;
import jaxb.types.wsfvt.server.amazon.ListSearchResponse;
import jaxb.types.wsfvt.server.amazon.Merchant;
import jaxb.types.wsfvt.server.amazon.MultiOperation;
import jaxb.types.wsfvt.server.amazon.MultiOperationResponse;
import jaxb.types.wsfvt.server.amazon.NonNegativeIntegerWithUnits;
import jaxb.types.wsfvt.server.amazon.ObjectFactory;
import jaxb.types.wsfvt.server.amazon.Offer;
import jaxb.types.wsfvt.server.amazon.OfferAttributes;
import jaxb.types.wsfvt.server.amazon.OfferListing;
import jaxb.types.wsfvt.server.amazon.Offers;
import jaxb.types.wsfvt.server.amazon.OfferSummary;
import jaxb.types.wsfvt.server.amazon.OperationInformation;
import jaxb.types.wsfvt.server.amazon.OperationRequest;
import jaxb.types.wsfvt.server.amazon.Price;
import jaxb.types.wsfvt.server.amazon.PromotionalTag;
import jaxb.types.wsfvt.server.amazon.Request;
import jaxb.types.wsfvt.server.amazon.ResponseGroupInformation;
import jaxb.types.wsfvt.server.amazon.Review;
import jaxb.types.wsfvt.server.amazon.SavedForLaterItems;
import jaxb.types.wsfvt.server.amazon.SearchInside;
import jaxb.types.wsfvt.server.amazon.SearchResultsMap;
import jaxb.types.wsfvt.server.amazon.Seller;
import jaxb.types.wsfvt.server.amazon.SellerFeedback;
import jaxb.types.wsfvt.server.amazon.SellerListing;
import jaxb.types.wsfvt.server.amazon.SellerListingLookup;
import jaxb.types.wsfvt.server.amazon.SellerListingLookupRequest;
import jaxb.types.wsfvt.server.amazon.SellerListingLookupResponse;
import jaxb.types.wsfvt.server.amazon.SellerListings;
import jaxb.types.wsfvt.server.amazon.SellerListingSearch;
import jaxb.types.wsfvt.server.amazon.SellerListingSearchRequest;
import jaxb.types.wsfvt.server.amazon.SellerListingSearchResponse;
import jaxb.types.wsfvt.server.amazon.SellerLookup;
import jaxb.types.wsfvt.server.amazon.SellerLookupRequest;
import jaxb.types.wsfvt.server.amazon.SellerLookupResponse;
import jaxb.types.wsfvt.server.amazon.Sellers;
import jaxb.types.wsfvt.server.amazon.SimilarityLookup;
import jaxb.types.wsfvt.server.amazon.SimilarityLookupRequest;
import jaxb.types.wsfvt.server.amazon.SimilarityLookupResponse;
import jaxb.types.wsfvt.server.amazon.SimilarProducts;
import jaxb.types.wsfvt.server.amazon.StringWithUnits;
import jaxb.types.wsfvt.server.amazon.Tracks;
import jaxb.types.wsfvt.server.amazon.Transaction;
import jaxb.types.wsfvt.server.amazon.TransactionItem;
import jaxb.types.wsfvt.server.amazon.TransactionLookup;
import jaxb.types.wsfvt.server.amazon.TransactionLookupRequest;
import jaxb.types.wsfvt.server.amazon.TransactionLookupResponse;
import jaxb.types.wsfvt.server.amazon.Transactions;
import jaxb.types.wsfvt.server.amazon.Variations;
import jaxb.types.wsfvt.server.amazon.VariationSummary;
import jaxb.types.wsfvt.server.amazon.AWSECommerceServicePortType;

/** 
 * This is the client that will be used to interact with the test's
 * Amazon AWSE implementation.
 */
public class AWSECommerceServiceClient {

    /**
     * This is a no argument constructor.
     */
    public AWSECommerceServiceClient() {
    }

    /**
     * This is the main method.
     *
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        AWSECommerceServiceClient client = new AWSECommerceServiceClient();
        String response = client.help();
        System.out.println("response = " + response);
        response = client.multiOperation();
        System.out.println("response = " + response);
    }

    /**
     * This is the help method client implementation.
     */
    public String help() {

        AWSECommerceService service = new AWSECommerceService();
        AWSECommerceServicePortType port = service.getAWSECommerceServicePort();

        jaxb.types.wsfvt.server.amazon.ObjectFactory objFactory1 =
            new jaxb.types.wsfvt.server.amazon.ObjectFactory();

        HelpRequest hr = objFactory1.createHelpRequest();
        hr.setAbout("about");
        hr.setHelpType("helpType");
        List<String> l1 = hr.getResponseGroup();
        l1.add("rg1");
        l1.add("rg2");
        
        List<HelpRequest> hrList = new ArrayList();
        hrList.add(hr);
        
        Holder<OperationRequest> operationRequest = new Holder<OperationRequest>(new OperationRequest());
        Holder<List<Information>> listInfoHolder = null;

        port.help("subscriptionId_1",
                  "associateTag_1",
                  "validate_1",
                  hr,
                  hrList,
                  operationRequest,
                  listInfoHolder);

        OperationRequest or = operationRequest.value;

        String requestProcessingTime = "Request Processing Time = "
                                       + or.getRequestProcessingTime();

        return requestProcessingTime;

    }

    /**
     * This is the multiOperation client implementation.
     */
    public String multiOperation() {

        AWSECommerceService service = new AWSECommerceService();
        AWSECommerceServicePortType port = service.getAWSECommerceServicePort();

        jaxb.types.wsfvt.server.amazon.ObjectFactory objFactory1 =
            new jaxb.types.wsfvt.server.amazon.ObjectFactory();

        Help help = objFactory1.createHelp();
        
        help.setSubscriptionId("SubscriptionId");
        help.setAssociateTag("AssociateTag");
        help.setValidate("Validate");

        HelpRequest hr = objFactory1.createHelpRequest();
        hr.setAbout("about");
        hr.setHelpType("helpType");
        List<String> l1 = hr.getResponseGroup();
        l1.add("rg1");
        l1.add("rg2");
        
        List<HelpRequest> hrList = help.getRequest();
        hrList.add(hr);

        ItemSearch itemSearch = objFactory1.createItemSearch();
        itemSearch.setSubscriptionId("SubscriptionId");
        itemSearch.setAssociateTag("AssociateTag");
        itemSearch.setXMLEscaping("XMLEscaping");
        itemSearch.setValidate("Validate");

        ItemSearchRequest isr = objFactory1.createItemSearchRequest();
        isr.setManufacturer("manufacturer");
        isr.setMaximumPrice(new BigInteger("101"));
        isr.setMerchantId("merchantId");
        isr.setMinimumPrice(new BigInteger("102"));
        isr.setMusicLabel("musicLabel");
        isr.setNeighborhood("neighborhood");
        isr.setOrchestra("orchestra");
        isr.setPostalCode("postalCode");
        isr.setPower("power");
        isr.setPublisher("publisher");
        List<String> isrRg = isr.getResponseGroup();
        isrRg.add("rg123");
        isr.setSearchIndex("searchIndex");
        isr.setSort("sort");
        isr.setState("state");
        isr.setTextStream("textStream");
        isr.setTitle("title");

        itemSearch.setShared(isr);
        
        List<ItemSearchRequest> lIsr = itemSearch.getRequest();
        lIsr.add(isr);

        ItemLookup itemLookup = objFactory1.createItemLookup();
        itemLookup.setSubscriptionId("SubscriptionId");
        itemLookup.setAssociateTag("AssociateTag");
        itemLookup.setXMLEscaping("XMLEscaping");
        itemLookup.setValidate("Validate");

        ItemLookupRequest ilr = objFactory1.createItemLookupRequest();
        ilr.setCondition("condition");
        ilr.setDeliveryMethod("deliveryDate");
        ilr.setFutureLaunchDate("futureLaunchDate");
        ilr.setIdType("idType");
        ilr.setISPUPostalCode("ispuPostalCode");
        ilr.setMerchantId("merchantId");
        ilr.setOfferPage(new BigInteger("103"));
        List<String> ilrl1 = ilr.getItemId();
        ilrl1.add("itemId");
        List<String> ilrRg = ilr.getResponseGroup();
        isrRg.add("rg1234");
        ilr.setReviewPage(new BigInteger("104"));
        ilr.setSearchIndex("searchIndex");
        ilr.setSearchInsideKeywords("searchInsideKeywords");
        ilr.setVariationPage(new BigInteger("105"));

        itemLookup.setShared(ilr);
        
        List<ItemLookupRequest> lIlr = itemLookup.getRequest();
        lIlr.add(ilr);

        ListSearch listSearch = objFactory1.createListSearch();
        listSearch.setSubscriptionId("SubscriptionId");
        listSearch.setAssociateTag("AssociateTag");
        listSearch.setXMLEscaping("XMLEscaping");
        listSearch.setValidate("Validate");

        ListSearchRequest lsr = objFactory1.createListSearchRequest();
        lsr.setCity("Austin");
        lsr.setEmail("webservices@us.ibm.com");
        lsr.setFirstName("Web");
        lsr.setLastName("Services");
        lsr.setListPage(new BigInteger("106"));
        lsr.setListType("listType");
        lsr.setName("name");
        List<String> lsrRg = lsr.getResponseGroup();
        lsrRg.add("rg12345");
        lsr.setState("TX");
        
        listSearch.setShared(lsr);

        List<ListSearchRequest> lLsr = listSearch.getRequest();
        lLsr.add(lsr);

        ListLookup listLookup = objFactory1.createListLookup();
        listLookup.setSubscriptionId("SubscriptionId");
        listLookup.setAssociateTag("AssociateTag");
        listLookup.setXMLEscaping("XMLEscaping");
        listLookup.setValidate("Validate");
        
        ListLookupRequest llr = objFactory1.createListLookupRequest();
        llr.setCondition("condition");
        llr.setDeliveryMethod("deliveryMethod");
        llr.setISPUPostalCode("postalCode");
        llr.setListId("listId");
        llr.setListType("listType");
        llr.setMerchantId("merchantId");
        llr.setProductGroup("productGroup");
        llr.setProductPage(new BigInteger("107"));
        List<String> llrRg = llr.getResponseGroup();
        llrRg.add("rg123456");
        llr.setSort("sort");

        listLookup.setShared(llr);

        List<ListLookupRequest> lLlr = listLookup.getRequest();
        lLlr.add(llr);

        CustomerContentSearch customerContentSearch = objFactory1.createCustomerContentSearch();
        customerContentSearch.setSubscriptionId("SubscriptionId");
        customerContentSearch.setAssociateTag("AssociateTag");
        customerContentSearch.setXMLEscaping("XMLEscaping");
        customerContentSearch.setValidate("Validate");
        
        CustomerContentSearchRequest ccsr = objFactory1.createCustomerContentSearchRequest();
        ccsr.setCustomerPage(new BigInteger("108"));
        ccsr.setEmail("email");
        ccsr.setName("name");
        List<String> ccsrRg = ccsr.getResponseGroup();
        ccsrRg.add("rg1234567");

        customerContentSearch.setShared(ccsr);
        
        List<CustomerContentSearchRequest> lCcsr = customerContentSearch.getRequest();
        lCcsr.add(ccsr);

        CustomerContentLookup customerContentLookup = objFactory1.createCustomerContentLookup();
        customerContentLookup.setSubscriptionId("SubscriptionId");
        customerContentLookup.setAssociateTag("AssociateTag");
        customerContentLookup.setXMLEscaping("XMLEscaping");
        customerContentLookup.setValidate("Validate");
        
        CustomerContentLookupRequest cclr = objFactory1.createCustomerContentLookupRequest();
        cclr.setCustomerId("customerId");
        List<String> cclrRg = cclr.getResponseGroup();
        cclrRg.add("rg12345678");
        cclr.setReviewPage(new BigInteger("109"));
        
        customerContentLookup.setShared(cclr);

        List<CustomerContentLookupRequest> lCclr = customerContentLookup.getRequest();
        lCclr.add(cclr);

        SimilarityLookup similarityLookup = objFactory1.createSimilarityLookup();
        similarityLookup.setSubscriptionId("SubscriptionId");
        similarityLookup.setAssociateTag("AssociateTag");
        similarityLookup.setXMLEscaping("XMLEscaping");
        similarityLookup.setValidate("Validate");
        
        SimilarityLookupRequest slr = objFactory1.createSimilarityLookupRequest();
        slr.setCondition("condition");
        slr.setDeliveryMethod("deliveryMethod");
        List<String> slrId = slr.getItemId();
        slr.setISPUPostalCode("postalCode");
        slr.setMerchantId("merchantId");
        List<String> slrRg = slr.getResponseGroup();
        slrRg.add("rg123456789");
        slr.setSimilarityType("similarityType");
        
        similarityLookup.setShared(slr);

        List<SimilarityLookupRequest> lSlr = similarityLookup.getRequest();
        lSlr.add(slr);

        SellerLookup sellerLookup = objFactory1.createSellerLookup();
        sellerLookup.setSubscriptionId("SubscriptionId");
        sellerLookup.setAssociateTag("AssociateTag");
        sellerLookup.setXMLEscaping("XMLEscaping");
        sellerLookup.setValidate("Validate");

        SellerLookupRequest slr2 = objFactory1.createSellerLookupRequest();
        List<String> slrRg2 = slr2.getResponseGroup();
        slrRg2.add("listing1");
        slrRg2.add("listing2");
        List<String> slr2Id = slr2.getSellerId();
        slr2Id.add("id1");
        slr2Id.add("id2");
        slr2.setFeedbackPage(new BigInteger("1223"));
        
        sellerLookup.setShared(slr2);
        
        List<SellerLookupRequest> lSlr2 = sellerLookup.getRequest();
        lSlr2.add(slr2);

        CartGet cartGet = objFactory1.createCartGet();
        cartGet.setSubscriptionId("SubscriptionId");
        cartGet.setAssociateTag("AssociateTag");
        cartGet.setXMLEscaping("XMLEscaping");
        cartGet.setValidate("Validate");

        CartGetRequest cgr = objFactory1.createCartGetRequest();
        cgr.setCartId("cartId");
        cgr.setHMAC("HMAC");
        cgr.setMergeCart("mergeCart");
        List<String> cgrRg = cgr.getResponseGroup();
        cgrRg.add("rg12345678910");

        cartGet.setShared(cgr);

        List<CartGetRequest> lCgr = cartGet.getRequest();
        lCgr.add(cgr);

        CartAdd cartAdd = objFactory1.createCartAdd();
        cartAdd.setSubscriptionId("SubscriptionId");
        cartAdd.setAssociateTag("AssociateTag");
        cartAdd.setXMLEscaping("XMLEscaping");
        cartAdd.setValidate("Validate");

        CartAddRequest car = objFactory1.createCartAddRequest();
        car.setCartId("cartId");
        car.setHMAC("HMAC");
        car.setMergeCart("mergeCart");
        List<String> carRg = car.getResponseGroup();
        carRg.add("rg12345678910");

        cartAdd.setShared(car);

        List<CartAddRequest> lCar = cartAdd.getRequest();
        lCar.add(car);

        CartCreate cartCreate = objFactory1.createCartCreate();
        cartCreate.setSubscriptionId("SubscriptionId");
        cartCreate.setAssociateTag("AssociateTag");
        cartCreate.setXMLEscaping("XMLEscaping");
        cartCreate.setValidate("Validate");

        CartCreateRequest ccr = objFactory1.createCartCreateRequest();
        ccr.setMergeCart("setMergeCart");
        List<String> ccrRg = ccr.getResponseGroup();
        ccrRg.add("listing1");
        ccrRg.add("listing2");

        cartCreate.setShared(ccr);

        List<CartCreateRequest> lCcr = cartCreate.getRequest();
        lCcr.add(ccr);

        CartModify cartModify = objFactory1.createCartModify();
        cartModify.setSubscriptionId("SubscriptionId");
        cartModify.setAssociateTag("AssociateTag");
        cartModify.setXMLEscaping("XMLEscaping");
        cartModify.setValidate("Validate");

        CartModifyRequest cmr = objFactory1.createCartModifyRequest();
        cmr.setCartId("CartId");
        cmr.setHMAC("HMAC");
        cmr.setMergeCart("MergeCart");
        
        CartModifyRequest.Items cmrItem = objFactory1.createCartModifyRequestItems();
        
        List<CartModifyRequest.Items.Item> cmrLItem = cmrItem.getItem();

        CartModifyRequest.Items.Item cmrItems = objFactory1.createCartModifyRequestItemsItem();

        cmrItems.setAction("action");
        cmrItems.setCartItemId("CartItemId");
        cmrItems.setQuantity(new BigInteger("2342"));
        
        cmrLItem.add(cmrItems);

        cmr.setItems(cmrItem);

        List<String> cmrRg = cmr.getResponseGroup();
        cmrRg.add("listing1");
        cmrRg.add("listing2");
        
        cartModify.setShared(cmr);

        List<CartModifyRequest> lCmr = cartModify.getRequest();
        lCmr.add(cmr);

        CartClear cartClear = objFactory1.createCartClear();
        cartClear.setSubscriptionId("SubscriptionId");
        cartClear.setAssociateTag("AssociateTag");
        cartClear.setXMLEscaping("XMLEscaping");
        cartClear.setValidate("Validate");
        
        CartClearRequest ccr2 = objFactory1.createCartClearRequest();
        ccr2.setCartId("CartId");
        ccr2.setHMAC("HMAC");
        ccr2.setMergeCart("MergeCart");
        List<String> ccrrRg = ccr2.getResponseGroup();
        ccrrRg.add("listing1");
        ccrrRg.add("listing2");

        cartClear.setShared(ccr2);

        List<CartClearRequest> lCcr2 = cartClear.getRequest();
        lCcr2.add(ccr2);

        TransactionLookup transactionLookup = objFactory1.createTransactionLookup();
        transactionLookup.setSubscriptionId("SubscriptionId");
        transactionLookup.setAssociateTag("AssociateTag");
        transactionLookup.setXMLEscaping("XMLEscaping");
        transactionLookup.setValidate("Validate");

        TransactionLookupRequest tlr = objFactory1.createTransactionLookupRequest();
        List<String> tlrRg = tlr.getResponseGroup();
        tlrRg.add("listing1");
        tlrRg.add("listing2");
        List<String> tlrTId = tlr.getTransactionId();
        tlrTId.add("transactionID1");
        tlrTId.add("transactionID2");
        
        transactionLookup.setShared(tlr);

        List<TransactionLookupRequest> lTlr = transactionLookup.getRequest();
        lTlr.add(tlr);

        SellerListingSearch sellerListingSearch = objFactory1.createSellerListingSearch();
        sellerListingSearch.setSubscriptionId("SubscriptionId");
        sellerListingSearch.setAssociateTag("AssociateTag");
        sellerListingSearch.setXMLEscaping("XMLEscaping");
        sellerListingSearch.setValidate("Validate");

        SellerListingSearchRequest slsr = objFactory1.createSellerListingSearchRequest();
        slsr.setBrowseNode("BrowseNode");
        slsr.setCountry("Country");
        slsr.setKeywords("Keywords");
        slsr.setListingPage(new BigInteger("121"));
        slsr.setOfferStatus("OfferStatus");
        slsr.setPostalCode("PostalCode");
        List<String> slsrRg = slsr.getResponseGroup();
        slsrRg.add("listing1");
        slsrRg.add("listing2");
        slsr.setSearchIndex("SearchIndex");
        slsr.setSellerId("SellerId");
        slsr.setShipOption("ShipOption");
        slsr.setSort("Sort");
        slsr.setTitle("Title");
        
        sellerListingSearch.setShared(slsr);

        List<SellerListingSearchRequest> lSlsr = sellerListingSearch.getRequest();
        lSlsr.add(slsr);

        SellerListingLookup sellerListingLookup = objFactory1.createSellerListingLookup();
        sellerListingLookup.setSubscriptionId("SubscriptionId");
        sellerListingLookup.setAssociateTag("AssociateTag");
        sellerListingLookup.setXMLEscaping("XMLEscaping");
        sellerListingLookup.setValidate("Validate");

        SellerListingLookupRequest sllr = objFactory1.createSellerListingLookupRequest();
        sllr.setId("id");
        sllr.setIdType("IdType");
        List<String> sllrRg = sllr.getResponseGroup();
        sllrRg.add("listing1");
        sllrRg.add("listing2");

        sellerListingLookup.setShared(sllr);

        List<SellerListingLookupRequest> lSllr = sellerListingLookup.getRequest();
        lSllr.add(sllr);

        BrowseNodeLookup browseNodeLookup = objFactory1.createBrowseNodeLookup();
        browseNodeLookup.setSubscriptionId("SubscriptionId");
        browseNodeLookup.setAssociateTag("AssociateTag");
        browseNodeLookup.setXMLEscaping("XMLEscaping");
        browseNodeLookup.setValidate("Validate");

        Holder<OperationRequest> operationRequest = new Holder<OperationRequest>(new OperationRequest());
        Holder<HelpResponse> helpResponse = null;
        Holder<ItemSearchResponse> itemSearchResponse = null;
        Holder<ItemLookupResponse> itemLookupResponse = null;
        Holder<ListSearchResponse> listSearchResponse = null;
        Holder<ListLookupResponse> listLookupResponse = null;
        Holder<CustomerContentSearchResponse> customerContentSearchResponse = null;
        Holder<CustomerContentLookupResponse> customerContentLookupResponse = null;
        Holder<SimilarityLookupResponse> similarityLookupResponse = null;
        Holder<SellerLookupResponse> sellerLookupResponse = null;
        Holder<CartGetResponse> cartGetResponse = null;
        Holder<CartAddResponse> cartAddResponse = null;
        Holder<CartCreateResponse> cartCreateResponse = null;
        Holder<CartModifyResponse> cartModifyResponse = null;
        Holder<CartClearResponse> cartClearResponse = null;
        Holder<TransactionLookupResponse> transactionLookupResponse = null;
        Holder<SellerListingSearchResponse> sellerListingSearchResponse = null;
        Holder<SellerListingLookupResponse> sellerListingLookupResponse = null;
        Holder<BrowseNodeLookupResponse> browseNodeLookupResponse = null;
        
        port.multiOperation(help,
                            itemSearch,
                            itemLookup,
                            listSearch,
                            listLookup,
                            customerContentSearch,
                            customerContentLookup,
                            similarityLookup,
                            sellerLookup,
                            cartGet,
                            cartAdd,
                            cartCreate,
                            cartModify,
                            cartClear,
                            transactionLookup,
                            sellerListingSearch,
                            sellerListingLookup,
                            browseNodeLookup,
                            operationRequest,
                            helpResponse,
                            itemSearchResponse,
                            itemLookupResponse,
                            listSearchResponse,
                            listLookupResponse,
                            customerContentSearchResponse,
                            customerContentLookupResponse,
                            similarityLookupResponse,
                            sellerLookupResponse,
                            cartGetResponse,
                            cartAddResponse,
                            cartCreateResponse,
                            cartModifyResponse,
                            cartClearResponse,
                            transactionLookupResponse,
                            sellerListingSearchResponse,
                            sellerListingLookupResponse,
                            browseNodeLookupResponse);

        OperationRequest or = operationRequest.value;

        String requestProcessingTime = "Request Processing Time = "
                                       + or.getRequestProcessingTime();

        return requestProcessingTime;

    }

}
