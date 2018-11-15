package com.yd.ydsp.common.security.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * ��������Ϣ��<a href="http://www.alisoup.net/attachment/%E6%94%AF%E4%BB%98%E5%AE%9D%E4%BC%9A%E5%91%98%E4%BF%A1%E6%81%AF%E5%B1%95%E7%A4%BA%E8%A7%84%E8%8C%83.docx">��֧������Ա��Ϣչʾ�淶></a> ���в������صĹ����ࡣ
 * ����˵��������������ת����:����ԭ���� + *** ����ʽ���� 130****5634 <br/>
 * <br/>
 * <strong> ��<code>hideFlag == true </code>ʱ�����Σ���Ŀ���Խ׶ο��Թرտ��أ����Խ������ٿ�����</strong> <br/>
 * <br/>
 * ʾ����
 * <pre>
 * ���п��ţ�
 * SensitiveDataUtil.bankCardNohide("612526198319131434") = "612526********1434"
 * <br/>
 * ���֤�ţ�
 * SensitiveDataUtil.idCardNohide("362397201201012384") = "36239***********84"
 * <br/>
 * ���䣺
 * SensitiveDataUtil.emailHide("yaaachaa@163.com") = "yaa***@163.com"
 * SensitiveDataUtil.emailHideSMS("yaaachaa@163.com") = "yaa*@163.com"
 * SensitiveDataUtil.emailHideSMS("yaaachaa@netvigator.com") = "yaa*@netviga*"
 * <br/>
 * �绰���루�ֻ���̶��绰��--��վ�Լ��ͻ���
 * SensitiveDataUtil.phoneOrTelNoHide("15087653459") = "150****3459"
 * SensitiveDataUtil.phoneOrTelNoHide("0796-1234567") = "0796-***4567"
 * SensitiveDataUtil.phoneOrTelNoHide("01036852045") = "0103***2045"
 * 
 * �ֻ�����ͨ�����ع��򣨰����۰�̨�������������м���λ--��վ�Լ��ͻ���
 * SensitiveDataUtil.cellphoneHide("13071835358") = 130****5358
 * SensitiveDataUtil.cellphoneHide("3071835358") = 07****358
 * SensitiveDataUtil.cellphoneHide("071835358") = 71****58
 * SensitiveDataUtil.cellphoneHide("835358") = 8****8
 * �����ڶ�����
 * SensitiveDataUtil.cellphoneHideSMS("13071835358") = 130*5358
 * SensitiveDataUtil.cellphoneHideSMS("3071835358") = 07*358
 * SensitiveDataUtil.cellphoneHideSMS("071835358") = 71*58
 * SensitiveDataUtil.cellphoneHideSMS("835358") = 8*8
 * <br/>
 * ֧������¼����
 * SensitiveDataUtil.alipayLogonIdHide("15087653459") = "150****3459"
 * SensitiveDataUtil.alipayLogonIdHide("yaaachaa@163.com") = "yaa***@163.com"
 * <br/>
 * �������̶���ʽ������Ϣ�����η�����
 * SensitiveDataUtil.defualtHide("ttt") = "t*t" 
 * <br/>
 * �Զ������ι���չʾ��
 * SensitiveDataUtil.customizeHide("13568794561",3,4,4) = "135****4561" 
 * <br/>
 * ����ʼ��ǩ�ͽ�β��ǩ�м�����ݰ�ָ���������������ͽ��в�������:
 * SensitiveDataUtil.filterHide(final String sourceStr,final String tagBegin, final String tagEnd,final int sensitiveDataType )
 * sourceStrΪ��
 * 
 * <pre>
 *  &lt;Party id=&quot;part_2&quot;&gt;
 *   &lt;FullName&gt;yimin.jiang&lt;/FullName&gt;
 *   &lt;GovtIDTC tc=&quot;802&quot;&gt;&lt;/GovtIDTC&gt;
 *   &lt;GovtID&gt;432926198110191188&lt;/GovtID&gt;
 *   &lt;GovtTermDate&gt;2011-06-07&lt;/GovtTermDate&gt;
 *  &lt;/Party&gt;
 *  <br/>
 * <code>tagBegin: &lt;GovtID&gt;</code>
 * <code>tagEnd: &lt;/GovtID&gt;</code> 
 * <code>sensitiveInfoType: SensitiveDataUtil.IDCARDNO_DATA</code>
 * </pre>
 * 
 * ��������ֵ:
 * 
 * <pre>
 *  &lt;Party id=&quot;part_2&quot;&gt;
 *   &lt;FullName&gt;yimin.jiang&lt;/FullName&gt;
 *   &lt;GovtIDTC tc=&quot;802&quot;&gt;&lt;/GovtIDTC&gt;
 *   &lt;GovtID&gt;432926********1188&lt;/GovtID&gt;
 *   &lt;GovtTermDate&gt;2011-06-07&lt;/GovtTermDate&gt;
 *  &lt;/Party&gt;
 * </pre>
 */
public class SensitiveDataUtil {
    /** �Ƿ���������������εĿ��� */
    private static boolean       hideFlag          = true;

    // ������Ϣ���ͱ�ʶ����
    /** ���п����������ͱ�ʶ��������SensitiveDataUtil.filterHide������sensitiveDataType������ */
    public static final int      BANKCARDNO_DATA   = 0;
    /** ���֤���������ͱ�ʶ��������SensitiveDataUtil.filterHide������sensitiveDataType������ */
    public static final int      IDCARDNO_DATA     = 1;
    /** �绰�����������ͱ�ʶ��������SensitiveDataUtil.filterHide������sensitiveDataType������ */
    public static final int      PHONENO_DATA      = 2;
    /** EMAIL�������ͱ�ʶ��������SensitiveDataUtil.filterHide������sensitiveDataType������ */
    public static final int      EMAIL_DATA        = 3;

    /** ��½���֤��������ʽ */
    private static final String  ID_CARD_REGEXP    = "[0-9]{15}|[0-9]{18}|[0-9]{14}X|[0-9]{17}X";
    /** ���п���������ʽ */
    private static final String  BANK_CARD_REGEXP  = "[0-9]{13,19}";
    /** �ֻ����߹̶��绰������ʽ */
    private static final String  PHONE_TEL_REGEXP  = "[0-9]{3,4}[-]?[0-9]{7,8}";
    /** ��½���֤��ƥ��ģʽ */
    private static final Pattern ID_CARD_PATTERN   = Pattern.compile(ID_CARD_REGEXP);
    /** ���п���ƥ��ģʽ */
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile(BANK_CARD_REGEXP);
    /** �ֻ����߹̶��绰ƥ��ģʽ */
    private static final Pattern PHONE_TEL_PATTERN = Pattern.compile(PHONE_TEL_REGEXP);

    /**
     * ���캯��
     * 
     * @param hideFlag
     *            �Ƿ���Ҫ�������εĿ��أ�<code>ture</code>��ʾ�򿪡�
     */
    public SensitiveDataUtil(final boolean hideFlag) {
        SensitiveDataUtil.setHideFlag(hideFlag);
    }

    /**
     * ���캯����Ĭ�Ͽ������ο���
     */
    public SensitiveDataUtil() {
        SensitiveDataUtil.setHideFlag(true);
    }

    /**
     * ͨ���Ƿ���@���ţ����ж��Ƿ���Email��ַ��
     * 
     * @param email
     * @return
     */
    private static boolean isEmail(final String email) {
        return email.indexOf('@') > 0;
    }

    /**
     * ��֧������½�ʺŽ��в������ش���������ֻ��ţ���ʾǰ3�ͺ�4λ�������email��ֻ��ʾ�û�����ǰ3λ+*+@��������������ǣ���ʾǰ3�ͺ�4λ
     * ������7���ַ��ģ�ȫ����ʾ������7���ַ��ģ��м����'*'���棩��<br/>
     * 
     * @param logonId
     *            ���������ش����֧������½�ʺţ��������ֻ��Ż�email��
     * @return ���hideFlagΪtrue�����ذ��淶���в������غ�ĵ�¼�š�
     */
    public static String alipayLogonIdHide(final String logonId) {
        if (!needHide())
            return logonId;

        if (isBlank(logonId))
            return logonId;

        if (isEmail(logonId))
            return emailHide(logonId);
        else
            return cellphoneHide(logonId);
    }

    /**
     * ��֧������½�ʺŽ��в������ش��������ڶ��š�<br/>
     * 
     * @param logonId
     *            ���������ش����֧������½�ʺţ��������ֻ��Ż�email��
     * @return ���hideFlagΪtrue�����ذ��淶���в������غ�ĵ�¼�š�
     */
    public static String alipayLogonIdHideSMS(final String logonId) {
        if (!needHide())
            return logonId;

        if (isBlank(logonId))
            return logonId;

        if (isEmail(logonId))
            return emailHideSMS(logonId);
        else
            return cellphoneHideSMS(logonId);
    }

    /**
     * �Դ�½���֤�Ž��в������ش���ֻ��ʾǰ5λ�ͺ�2λ��������*���档<br/>
     * ���doValidateΪtrue�Ҵ�������ݲ��ǺϷ��Ĵ�½���֤�ţ�����������Ϣȱʡ���ط�ʽ������ʾǰ1/3�ͺ�1/3��������*���档<br/>
     * 
     * @param idCardNo
     *            ���������ش�������֤�š�
     * @param doValidate
     *            �Ƿ������֤�źϷ���У�顣���棺��У����������ƥ�䣬�����ϱȲ���У��ķ���������ġ�
     * @return ���hideFlagΪtrue�����ط��ϡ�֧������Ա��Ϣչʾ�淶�������֤�Ų���չʾ�ַ��������򷵻�ԭ���ݡ�
     * 
     */
    public static String idCardNoHide(final String idCardNo, final boolean doValidate) {
        if (!needHide())
            return idCardNo;

        if (isBlank(idCardNo))
            return idCardNo;

        if (doValidate) {
            if (!isIdCardNo(idCardNo)) {
                return defaultHide(idCardNo); // ���Ǵ�½���֤�ţ���ȱʡ��������ʾ������
            }
        }
        return customizeHide(idCardNo, 5, 2, idCardNo.length() - 7);
    }

    /**
     * �Դ�½���֤�Ž��в������ش���ļ�㷽����Ĭ�ϲ�����֤����ʹ�����Լ����ϡ�
     * 
     * @param idCardNo
     *            ���������ش�������֤�š�
     * @return ���hideFlagΪtrue�����ط��ϡ�֧������Ա��Ϣչʾ�淶�������֤�Ų���չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String idCardNoHide(final String idCardNo) {
        return idCardNoHide(idCardNo, false);
    }

    /**
     * �����п��Ž��в������ش���ֻ��ʾǰ6λ�ͺ�4λ��������*���档<br/>
     * ���doValidateΪtrue�Ҵ�������ݲ��ǺϷ������кţ�����������Ϣȱʡ���ط�ʽ������ʾǰ1/3�ͺ�1/3��������*���档<br/>
     * 
     * @param bankCardNo
     *            ���������ش�������п��š�
     * @param doValidate
     *            �Ƿ������п��źϷ���У�顣���棺��У����������ƥ�䣬�����ϱȲ���У��ķ���������ġ�
     * @return ���hideFlagΪtrue�����ط��ϡ�֧������Ա��Ϣչʾ�淶�������п��Ų���չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String bankCardNoHide(final String bankCardNo, final boolean doValidate) {
        if (!needHide())
            return bankCardNo;

        if (isBlank(bankCardNo))
            return bankCardNo;

        if (doValidate) {
            if (!isBankCardNo(bankCardNo)) {
                return defaultHide(bankCardNo); // �������п��ţ���ȱʡ��������ʾ������
            }
        }
        return customizeHide(bankCardNo, 6, 4, bankCardNo.length() - 10);
    }

    /**
     * �����п��Ž��в������ش���ļ�㷽����Ĭ�ϲ�����֤����ʹ�����Լ����ϡ�
     * 
     * @param bankCardNo
     *            ���������ش�������п��š�
     * @return ���hideFlagΪtrue�����ط��ϡ�֧������Ա��Ϣչʾ�淶�������п��Ų���չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String bankCardNoHide(final String bankCardNo) {
        return bankCardNoHide(bankCardNo, false);
    }

    /**
     * �Ե绰���루�ֻ��Ż��������룩���в������ش����ֻ���ֻ��ʾǰ3λ�ͺ�4λ���̶��绰����ֻ��ʾ���źͺ�4λ��������*���档<br/>
     * <strong> ��֧�ִ��ֻ��ĵ绰���� �������ڴ�½�����ֻ���</strong> <br/>
     * ���doValidateΪtrue�Ҵ�������ݲ��ǺϷ��ĵ绰���룬����������Ϣȱʡ���ط�ʽ������ʾǰ1/3�ͺ�1/3��������*���档
     * 
     * @param phoneOrTelNo
     *            �����ش���ĵ绰����
     * @param doValidate
     *            �Ƿ����绰����Ϸ���У�顣���棺��У����������ƥ�䣬�����ϱȲ���У��ķ���������ġ�
     * @return ���hideFlagΪtrue�����ء�֧������Ա��Ϣչʾ�淶�����Ƽ��ĵ绰���벿��չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String phoneOrTelNoHide(final String phoneOrTelNo, final boolean doValidate) {
        if (!needHide())
            return phoneOrTelNo;

        if (isBlank(phoneOrTelNo))
            return phoneOrTelNo;
        String tmp = phoneOrTelNo.trim();
        if (doValidate) {
            if (!isPhoneOrTelNo(tmp)) {
                return defaultHide(tmp); // ���ǵ绰���룬��ȱʡ��������ʾ������
            }
        }
        int frontCharNum = (tmp.charAt(0) == '1') ? 3 : 4; // phoneOrTelNo��1��ͷ����Ϊ���ֻ��ţ�ǰ3������ǰ4
        if (tmp.indexOf("-") > 0) { // �ǹ̶��绰����
            frontCharNum = tmp.indexOf("-") + 1;
        }
        return customizeHide(tmp, frontCharNum, 4, tmp.length() - 4 - frontCharNum);
    }

    /**
     * �Ե绰���루�ֻ��Ż��������룩���в������ش���ļ�㷽����Ĭ�ϲ�����֤����ʹ�����Լ����ϡ�
     * 
     * @param phoneOrTelNo
     *            �����ش���ĵ绰����
     * @return ���hideFlagΪtrue�����ء�֧������Ա��Ϣչʾ�淶�����Ƽ��ĵ绰���벿��չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String phoneOrTelNoHide(final String phoneOrTelNo) {
        return phoneOrTelNoHide(phoneOrTelNo, false);
    }

    /**
     * �ֻ�����ͨ�����ع��򣨰����۰�̨�������������м���λ ��������վ�Լ��ͻ���
     * <li>SensitiveDataUtil.cellphoneHide("13071835358") = 130****5358
     * <li>SensitiveDataUtil.cellphoneHide("3071835358") = 307****358
     * <li>SensitiveDataUtil.cellphoneHide("071835358") = 07****358
     * <li>SensitiveDataUtil.cellphoneHide("835358") = 8****8
     * 
     * @param cellphone
     * @return ���غ���ֻ�����
     */
    public static String cellphoneHide(final String cellphone) {
        if (!needHide())
            return cellphone;

        if (isBlank(cellphone))
            return cellphone;
        String tmp = cellphone.trim();
        int notHideNum = tmp.length() - 4;
        return customizeHide(tmp, notHideNum / 2, notHideNum - notHideNum / 2, 4);
    }

    /**
     * �ֻ�����ͨ�����ع��򣨰����۰�̨�������� �����ڶ��š�
     * SensitiveDataUtil.cellphoneHideSMS("13071835358") = 130*5358
     * SensitiveDataUtil.cellphoneHideSMS("3071835358") = 307*358
     * SensitiveDataUtil.cellphoneHideSMS("071835358") = 71*358
     * SensitiveDataUtil.cellphoneHideSMS("835358") = 8*8
     * 
     * @param cellphone
     * @return ���غ���ֻ�����
     */
    public static String cellphoneHideSMS(final String cellphone) {
        if (!needHide())
            return cellphone;

        if (isBlank(cellphone))
            return cellphone;
        String tmp = cellphone.trim();
        int notHideNum = tmp.length() - 4;
        return customizeHide(tmp, notHideNum / 2, notHideNum - notHideNum / 2, 1);

    }

    /**
     * ��Email���в������ش���ֻ��ʾ�û�����ǰ3λ+***+@���������û�������3λ������ʾ�û���ȫ��+***+@������</br>
     * ���doValidateΪtrue�Ҵ�������ݲ���email��������@����,����������Ϣȱʡ���ط�ʽ������ʾǰ1/3�ͺ�1/3��
     * 
     * @param email
     *            �������Email
     * @param doValidate
     *            �Ƿ���Email�Ϸ���У�顣���棺��У����������ƥ�䣬�����ϱȲ���У��ķ���������ġ�
     * @return ���hideFlagΪtrue�����ء�֧������Ա��Ϣչʾ�淶�����Ƽ���Email����չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String emailHide(final String email, final boolean doValidate) {
        if (!needHide())
            return email;

        if (isBlank(email))
            return email;

        if (doValidate) {
            if (!isEmail(email)) {
                return defaultHide(email); // ����email�˺ţ���ȱʡ��������ʾ������
            }
        }
        String tmp = email.trim();
        int atPos = tmp.indexOf('@');
        int frontNum = atPos < 3 ? atPos : 3;
        return customizeHide(tmp, frontNum, tmp.length() - atPos, 3);
    }

    /**
     * ��Email���в������ش���ļ�㷽����Ĭ�ϲ�����֤����ʹ�����Լ����ϡ�
     * 
     * @param email
     *            �������Email
     * @return ���hideFlagΪtrue�����ء�֧������Ա��Ϣչʾ�淶�����Ƽ���Email����չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String emailHide(final String email) {
        return emailHide(email, false);
    }

    /**
     * �Զ����е�Email���в������ش���ֻ��ʾ�û�����ǰ3λ+*+@���������û�������3λ������ʾ�û���ȫ��+*+@������</br>
     * ���doValidateΪtrue�Ҵ�������ݲ���email��������@����,����������Ϣȱʡ���ط�ʽ������ʾǰ1/3�ͺ�1/3��
     * 
     * @param email
     *            �������Email
     * @param doValidate
     *            �Ƿ���Email�Ϸ���У�顣���棺��У����������ƥ�䣬�����ϱȲ���У��ķ���������ġ�
     * @return ���hideFlagΪtrue�����ء�֧������Ա��Ϣչʾ�淶�����Ƽ���Email����չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String emailHideSMS(final String email, final boolean doValidate) {
        if (!needHide())
            return email;

        if (isBlank(email))
            return email;

        if (doValidate) {
            if (!isEmail(email)) {
                return defaultHide(email); // ����email�˺ţ���ȱʡ��������ʾ������
            }
        }
        StringBuilder result = new StringBuilder();
        String tmp = email.trim();
        int atPos = tmp.indexOf('@');
        int frontNum = atPos < 3 ? atPos : 3;
        result.append(tmp.substring(0, frontNum));
        result.append("*@");
        String backStr = tmp.substring(atPos + 1);
        int dotIndex = backStr.indexOf('.');
        if (dotIndex <= 7) {
            result.append(backStr.substring(0, dotIndex)).append(".*");
        } else {
            result.append(backStr.substring(0, 7)).append("*");
        }
        return result.toString();

    }

    /**
     * �Զ����е�Email���в������ش���ļ�㷽����Ĭ�ϲ�����֤����ʹ�����Լ����ϡ� <li>
     * SensitiveDataUtil.emailHideSMS("ddddddd@hide.com") = ddd*@hide.* <li>
     * SensitiveDataUtil.emailHideSMS("ddddddd@netvigator.com") = ddd*@netviga*
     * <li>SensitiveDataUtil.emailHideSMS("d@netvigator.com") = d*@netviga* <li>
     * SensitiveDataUtil.emailHideSMS("ddddddd@outlook.com") = ddd*@outlook.*
     * 
     * @param email
     *            �������Email
     * @return ���hideFlagΪtrue�����ء�֧������Ա��Ϣչʾ�淶�����Ƽ���Email����չʾ�ַ��������򷵻�ԭ���ݡ�
     */
    public static String emailHideSMS(final String email) {
        return emailHideSMS(email, false);
    }

    /**
     * <p>
     * �����ַ����е�������Ϣ������ʼ��ǩ<code>tagBegin</code>�ͽ�β��ǩ<code>tagEnd</code>
     * �м�����ݰ�ָ����������������<code>sensitiveInfoType</code>���в������ء�
     * </p>
     * <p>
     * ʾ��: ����֤����<br/>
     * <br/>
     * sourceStrΪ��
     * 
     * <pre>
     *  &lt;Party id=&quot;part_2&quot;&gt;
     *   &lt;FullName&gt;yimin.jiang&lt;/FullName&gt;
     *   &lt;GovtIDTC tc=&quot;802&quot;&gt;&lt;/GovtIDTC&gt;
     *   &lt;GovtID&gt;432926201110191188&lt;/GovtID&gt;
     *   &lt;GovtTermDate&gt;2011-06-07&lt;/GovtTermDate&gt;
     *  &lt;/Party&gt;
     * </pre>
     * 
     * <pre>
     * <code>tagBegin:&lt;GovtID&gt;</code>
     * <code>tagEnd: &lt;/GovtID&gt;</code>
     * <code>sensitiveInfoType: SensitiveDataUtil.IDCARDNO_DATA</code>
     * </pre>
     * 
     * ��������ֵ:
     * 
     * <pre>
     *  &lt;Party id=&quot;part_2&quot;&gt;
     *   &lt;FullName&gt;yimin.jiang&lt;/FullName&gt;
     *   &lt;GovtIDTC tc=&quot;802&quot;&gt;&lt;/GovtIDTC&gt;
     *   &lt;GovtID&gt;432926********1188&lt;/GovtID&gt;
     *   &lt;GovtTermDate&gt;2011-06-07&lt;/GovtTermDate&gt;
     *  &lt;/Party&gt;
     * </pre>
     * 
     * @param sourceStr
     *            Դ�ַ���
     * @param tagBegin
     *            ��ʼ��ǩ
     * @param tagEnd
     *            ��β��ǩ
     * @param sensitiveDataType
     *            �����������ͣ�ֵ��Ϊ��
     *            <code>SensitiveDataUtil.BANKCARDNO_DATA<code>��<code>SensitiveDataUtil.IDCARDNO_DATA<code>��<code>SensitiveDataUtil.PHONENO_DATA<code>��<code>SensitiveDataUtil.EMAIL_DATA<code>��<code>SensitiveDataUtil.UNKNOWN_DATA<code>��
     * @return ���hideFlagΪtrue�����ع��˵��������ݺ���ַ��������򷵻�ԭ���ݡ�
     */
    public static String filterHide(final String sourceStr, final String tagBegin,
                                    final String tagEnd, final int sensitiveDataType) {
        if (!needHide())
            return sourceStr;

        if (isBlank(sourceStr))
            return sourceStr;

        StringBuilder tmp = new StringBuilder(sourceStr);
        StringBuilder target = new StringBuilder();
        int begin = tmp.indexOf(tagBegin);
        int end = tmp.indexOf(tagEnd);
        // ����ҵ�ÿһ��ƥ��Ľڵ㣬Ȼ�󽫽ڵ������滻
        while (begin != -1 && end != -1) {
            // ˵������Ҫ��Ľڵ�
            target = target.append(tmp.toString().toCharArray(), 0, begin + tagBegin.length());
            // ��������ַ�
            String coverReplace = "";
            switch (sensitiveDataType) {
                case BANKCARDNO_DATA:
                    coverReplace = bankCardNoHide(tmp.substring(begin + tagBegin.length(), end));
                    break;
                case IDCARDNO_DATA:
                    coverReplace = idCardNoHide(tmp.substring(begin + tagBegin.length(), end));
                    break;
                case PHONENO_DATA:
                    coverReplace = phoneOrTelNoHide(tmp.substring(begin + tagBegin.length(), end));
                    break;
                case EMAIL_DATA:
                    coverReplace = emailHide(tmp.substring(begin + tagBegin.length(), end));
                    break;
                default:
                    coverReplace = defaultHide(tmp.substring(begin + tagBegin.length(), end));
                    break;
            }
            target.append(coverReplace);
            target.append(tagEnd);
            // �û�δ���ҵ�ʣ�ಿ�����µ�Ŀ�괮������
            tmp = new StringBuilder(tmp.substring(end + tagEnd.length()));
            begin = tmp.indexOf(tagBegin);
            end = tmp.indexOf(tagEnd);
        }
        // �������һ��
        target.append(tmp);
        return target.toString();
    }

    /**
     * ��ʾ��/β��1λ���м��**
     * 
     * <li>SensitiveDataUtil.taobaoNickHide("�л�") = ��**�� <li>
     * SensitiveDataUtil.taobaoNickHide("�л���") = ��**�� <li>
     * SensitiveDataUtil.taobaoNickHide("�л�����") = ��**�� <li>
     * SensitiveDataUtil.taobaoNickHide("china") = c**a
     * 
     * @param sensitiveData
     *            �Ա��ǳ�
     * @return �������غ���ǳ�
     */
    public static String taobaoNickHide(final String sensitiveData) {
        if (!needHide())
            return sensitiveData;

        if (isBlank(sensitiveData)) {
            return sensitiveData;
        }
        String tmp = sensitiveData.trim();
        return customizeHide(tmp, 1, 1, 2);
    }

    /**
     * ������Ϣȱʡ���ط�ʽ������ʾǰ1/3�ͺ�1/3������*�Ŵ��档���ݳ��Ȳ��ܱ�3����ʱ����ʾǰceil[length/3.0]�ͺ�floor[
     * length/3.0]
     * 
     * <pre>
     * SensitiveDataUtil.defaultHide("ttt") = "t*t"
     * SensitiveDataUtil.defaultHide("tttttttt") = "ttt***tt"
     * </pre>
     * 
     * @param sensitiveData
     *            ���������ش����������Ϣ��
     * @return ���κ������; ���hideFlagΪflase������ԭ���ݡ�
     */
    public static String defaultHide(final String sensitiveData) {
        if (!needHide())
            return sensitiveData;

        if (isBlank(sensitiveData)) {
            return sensitiveData;
        }
        String tmp = sensitiveData.trim();
        int length = tmp.length();
        int headNum = (int) Math.ceil(length * 1 / 3.0);
        int tailNum = (int) Math.floor(length * 1 / 3.0);
        return customizeHide(tmp, headNum, tailNum, length - headNum - tailNum);
    }

    /**
     * �Զ�������λ��������λ��
     * 
     * <pre>
     * SensitiveDataUtil.customizeHide("13568794561",3,4,4) = "135****4561"
     * SensitiveDataUtil.customizeHide("13568794561",0,4,4) = "****4561"
     * SensitiveDataUtil.customizeHide("13568794561",3,0,4) = "135****"
     * SensitiveDataUtil.customizeHide("13568794561",3,0,8) = "135********"
     * </pre>
     * 
     * @param sensitiveData
     *            ԭ����
     * @param frontCharNum
     *            չʾǰ��λ
     * @param tailCharNum
     *            չʾ��λ
     * @param hiddenCharNum
     *            չʾ�Ǻ�*�ĸ���
     * @return �������ص����������ַ���
     */
    public static String customizeHide(final String sensitiveData, final int frontCharNum,
                                       final int tailCharNum, final int hiddenCharNum) {
        if (isBlank(sensitiveData)) {
            return sensitiveData;
        }
        String tmp = sensitiveData.trim();
        int length = tmp.length();
        // �Ϸ��Լ�飬����������Ϸ�������Դ��������
        if (frontCharNum < 0 || tailCharNum < 0 || hiddenCharNum < 0
            || frontCharNum + tailCharNum > length) {
            return tmp;
        }

        int beginIndex = frontCharNum - 1;
        int endIndex = length - tailCharNum;

        // ԭ����ǰ�벿��
        StringBuilder result = new StringBuilder();
        if (beginIndex >= 0 && beginIndex < length)
            result.append(tmp.substring(0, frontCharNum));

        // �м�*
        for (int i = 0; i < hiddenCharNum; i++)
            result.append('*');

        // ԭ���ݺ�벿��
        if (endIndex >= 0 && endIndex < length)
            result.append(tmp.substring(endIndex));

        return result.toString();
    }

    /**
     * ���ж��Ƿ�Ϊ���ַ���
     * 
     * @param str
     * @return
     */
    private static boolean isBlank(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * ͨ��������ʽ"[0-9]{15}|[0-9]{18}|[0-9]{14}X|[0-9]{17}X"���ж��Ƿ��ǺϷ��Ĵ�½���֤�š�
     * 
     * @param idCardNo
     * @return
     */
    private static boolean isIdCardNo(final String idCardNo) {
        if (isBlank(idCardNo))
            return false;
        else {
            Matcher matcher = ID_CARD_PATTERN.matcher(idCardNo.trim());
            return matcher.matches();
        }
    }

    /**
     * ͨ��������ʽ"[0-9]{13,19}"���ж��Ƿ��ǺϷ������п��š�
     * 
     * @param bankCardNo
     * @return ָʾ����Ƿ����п��ŵĲ���ֵ��
     */
    private static boolean isBankCardNo(final String bankCardNo) {
        if (isBlank(bankCardNo))
            return false;
        else {
            Matcher matcher = BANK_CARD_PATTERN.matcher(bankCardNo.trim());
            return matcher.matches();
        }
    }

    /**
     * ͨ��������ʽ"[0-9]{3,4}[-]?[0-9]{7,8}"���ж��Ƿ��ǺϷ��ĵ绰���롣
     * 
     * @param phoneOrTelNo
     * @return ָʾ����Ƿ�绰����Ĳ���ֵ��
     */
    private static boolean isPhoneOrTelNo(final String phoneOrTelNo) {
        if (isBlank(phoneOrTelNo))
            return false;
        else {
            Matcher matcher = PHONE_TEL_PATTERN.matcher(phoneOrTelNo.trim());
            return matcher.matches();
        }
    }

    /**
     * hideFlag setter
     * 
     * @param hideFlag
     */
    public static void setHideFlag(final boolean hideFlag) {
        SensitiveDataUtil.hideFlag = hideFlag;
    }

    /**
     * hideFlag getter
     * 
     * @return �Ƿ���Ҫ������������
     */
    public static boolean needHide() {
        return hideFlag;
    }

}
